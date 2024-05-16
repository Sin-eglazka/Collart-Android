package com.example.collart.PersonalPage.CreateOrder

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.collart.Auth.CurrentUser
import com.example.collart.Tools.FileConverter.FileConverter
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.NetworkSystem.SkillModule
import com.example.collart.NetworkSystem.ToolsModule
import com.example.collart.R
import com.example.collart.Tools.UI.DropDownClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class CreateOrderActivity : AppCompatActivity() {

    private var selectedPrograms = BooleanArray(1)
    private var programs = arrayOf("Программы")
    private val programsList = ArrayList<Int>()
    private var image: File? = null
    private var files: MutableList<File> = emptyList<File>().toMutableList()
    private var startDate: Long = 0
    private var endDate: Long = 0

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUploaded.setImageURI(uri)
            val path = FileConverter.getImagePathFromInputStreamUri(uri, this@CreateOrderActivity, "orderCover.jpg")
            image = path?.let { File(it) }
        }
    }

    private val pickFiles = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uris = result.data?.clipData?.let { clipData ->
                (0 until clipData.itemCount).map { index ->
                    clipData.getItemAt(index).uri
                }
            } ?: listOfNotNull(result.data?.data)
            files = getFilesFromUris(uris)

        }
    }
    private lateinit var spinnerExperience: Spinner
    private lateinit var programsView: TextView
    private lateinit var nameOrderView: EditText
    private lateinit var orderSpecialistView: TextView
    private lateinit var shortOrderDescriptionView: EditText
    private lateinit var descriptionOrderView: EditText
    private lateinit var dateStartView: TextView
    private lateinit var dateEndView: TextView
    private lateinit var imageUploaded: ImageView
    private lateinit var imageOrderView: TextView
    private lateinit var filesOrderView: TextView
    private lateinit var createOrderButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_order)

        val toolbar: Toolbar = findViewById(R.id.toolbarCreateOrderPage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Создать заказ"

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        spinnerExperience = findViewById(R.id.experienceOrderView)
        val states = Experience.entries.map { it.stringValue }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExperience.adapter = adapter

        nameOrderView = findViewById(R.id.nameOrderView)

        GlobalScope.launch(Dispatchers.Main) {
            val skills = SkillModule.getAllSkillsRu()
            orderSpecialistView = findViewById(R.id.orderSpecialistView)
            val professionClickListener = DropDownClickListener(this@CreateOrderActivity, skills) { selectedItem ->
                orderSpecialistView.text = selectedItem
            }
            orderSpecialistView.setOnClickListener(professionClickListener)
        }

        orderSpecialistView = findViewById(R.id.orderSpecialistView)
        val professionClickListener = DropDownClickListener(this@CreateOrderActivity, listOf()) { selectedItem ->
            orderSpecialistView.text = selectedItem
        }
        orderSpecialistView.setOnClickListener(professionClickListener)

        shortOrderDescriptionView = findViewById(R.id.shortOrderDescriptionView)

        descriptionOrderView = findViewById(R.id.descriptionOrderView)

        dateStartView = findViewById(R.id.dateStartView)
        dateStartView.setOnClickListener{
            showDatePicker(true)
        }
        dateEndView = findViewById(R.id.dateEndView)
        dateEndView.setOnClickListener {
            showDatePicker(false)
        }

        imageUploaded = findViewById(R.id.imageUploadedView)
        imageOrderView = findViewById(R.id.imageOrderView)
        imageOrderView.setOnClickListener {
            openImagePicker()
        }

        createOrderButton = findViewById(R.id.createOrderBtn)
        createOrderButton.setOnClickListener {
            createOrder()
        }

        initProgramsListView()

        filesOrderView = findViewById(R.id.filesOrder)
        filesOrderView.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple file selection
            }
            pickFiles.launch(intent)
        }

    }

    private fun getFileFromUri(uri: Uri, name: String): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile(name, null, cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun getFilesFromUris(uris: List<Uri>): MutableList<File> {
        val files = mutableListOf<File>()
        var filenames = ""
        for (i in uris.indices) {
            val file = getFileFromUri(uris[i], "file" + i.toString())
            val fileName = getFileNameFromUri(uris[i])
            filenames += fileName + ", "
            files.add(file)
        }
        filenames = filenames.dropLast(2)
        filesOrderView.text = filenames
        return files
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        val returnCursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    private fun createOrder(){
        if (nameOrderView.text.isNotEmpty() && orderSpecialistView.text.isNotEmpty()
            && shortOrderDescriptionView.text.isNotEmpty() && descriptionOrderView.text.isNotEmpty()
            && dateStartView.text.isNotEmpty() && dateEndView.text.isNotEmpty() && programsList.size > 0){

            GlobalScope.launch(Dispatchers.Main) {

                val tools: MutableList<String> = emptyList<String>().toMutableList()
                for (j in programsList.indices) {

                    tools.add(programs[programsList[j]])
                }

                val experience = Experience.fromNameToString(spinnerExperience.selectedItem.toString())

                val response = OrderModule.uploadProject(CurrentUser.token, nameOrderView.text.toString(), orderSpecialistView.text.toString(),
                    shortOrderDescriptionView.text.toString(), descriptionOrderView.text.toString(), experience,
                    (startDate / 1000).toString(), (endDate / 1000).toString(), image, files, tools)
                if (response == "ok"){
                    Toast.makeText(this@CreateOrderActivity, "Order was created!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
                else{
                    Toast.makeText(this@CreateOrderActivity, response, Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this@CreateOrderActivity, "Not all fields are filled", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun openImagePicker() {
        pickImage.launch("image/*")
    }

    private fun showDatePicker(start: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDay"
                if (start){
                    dateStartView.text = selectedDate
                    startDate = calendar.timeInMillis
                }
                else{
                    endDate = calendar.timeInMillis
                    dateEndView.text = selectedDate
                }

            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun initProgramsListView() {
        programsView = findViewById(R.id.programsOrderView)
        getAvailablePrograms()
        programs = arrayOf()
        selectedPrograms = BooleanArray(programs.size)

        programsView.setOnClickListener {
            // Initialize alert dialog
            val builder = AlertDialog.Builder(this)

            // Set title
            builder.setTitle("Выберите программы")

            // Set dialog non-cancelable
            builder.setCancelable(false)

            builder.setMultiChoiceItems(programs, selectedPrograms) { _, i, b ->
                // Check condition
                if (b) {
                    // When checkbox selected
                    // Add position in lang list
                    programsList.add(i)
                    // Sort array list
                    programsList.sort()
                } else {
                    // When checkbox unselected
                    // Remove position from langList
                    programsList.remove(i)
                }
            }

            builder.setPositiveButton("OK") { dialogInterface, _ ->
                // Initialize string builder
                val stringBuilder = StringBuilder()

                // Use for loop
                for (j in programsList.indices) {
                    // Concat array value
                    stringBuilder.append(programs[programsList[j]])
                    // Check condition
                    if (j != programsList.size - 1) {
                        // When j value not equal
                        // to lang list size - 1
                        // Add comma
                        stringBuilder.append(", ")
                    }
                }
                // Set text on textView
                programsView.text = stringBuilder.toString()
                dialogInterface.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialogInterface, _ ->
                // Dismiss dialog
                dialogInterface.dismiss()
            }

            builder.setNeutralButton("Clear All") { dialogInterface, _ ->
                // Use for loop
                for (j in selectedPrograms.indices) {
                    // Remove all selection
                    selectedPrograms[j] = false
                }
                // Clear language list
                programsList.clear()
                // Clear text view value
                programsView.text = "Программы"
                dialogInterface.dismiss()
            }
            // Show dialog
            builder.show()
        }
    }

    private fun getAvailablePrograms(){
        GlobalScope.launch(Dispatchers.Main) {
            val tools = ToolsModule.getAllTools(CurrentUser.token)
            programs = tools.map { tool -> tool.name }.toTypedArray()
            selectedPrograms = BooleanArray(programs.size)
        }
    }

}