package com.example.collart.Portfolio

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.example.collart.Auth.CurrentUser
import com.example.collart.FileConverter.FileConverter
import com.example.collart.NetworkSystem.PortfolioModule
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CreatePortfolioActivity : AppCompatActivity() {

    private lateinit var namePortfolioView: EditText
    private lateinit var descriptionPortfolioView: EditText
    private lateinit var imagePortfolioView: TextView
    private lateinit var imageUploadedView: ImageView
    private lateinit var filesPortfolioView: TextView
    private lateinit var createPortfolioBtn: Button

    private var image: File? = null
    private var files: MutableList<File> = emptyList<File>().toMutableList()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUploadedView.setImageURI(uri)
            imageUploadedView.visibility = View.VISIBLE
            val path = FileConverter.getImagePathFromInputStreamUri(uri, this@CreatePortfolioActivity, "orderCover.jpg")
            image = path?.let { File(it) }
            imagePortfolioView.setText(getFileNameFromUri(uri))
        }
        else{
            imagePortfolioView.setText("")
            imageUploadedView.visibility = View.GONE
            image = null
        }
    }

    private val pickFiles = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uris = result.data?.clipData?.let { clipData ->
                (0 until clipData.itemCount).map { index ->
                    clipData.getItemAt(index).uri
                }
            } ?: listOf(result.data?.data).filterNotNull()
            files = getFilesFromUris(uris)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_portfolio)

        val toolbar: Toolbar = findViewById(R.id.toolbarCreatePortfolioPage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("Создать портфолио")

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        namePortfolioView = findViewById(R.id.namePortfolioView)
        descriptionPortfolioView = findViewById(R.id.descriptionPortfolioView)
        imagePortfolioView = findViewById(R.id.imagePortfolioView)
        filesPortfolioView = findViewById(R.id.filesPortfolioView)
        createPortfolioBtn = findViewById(R.id.createPortfolioBtn)
        imageUploadedView = findViewById(R.id.imageUploadedView)


        imagePortfolioView.setOnClickListener {
            pickImage.launch("image/*")
        }

        filesPortfolioView.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple file selection
            }
            pickFiles.launch(intent)
        }

        createPortfolioBtn.setOnClickListener {

            createPortfolio()

        }
    }

    private fun createPortfolio() {
        if (image != null && namePortfolioView.text.toString() != "" && descriptionPortfolioView.text.toString() != "" && files.size != 0) {

            GlobalScope.launch((Dispatchers.Main)) {
                val response = PortfolioModule.uploadProject(
                    CurrentUser.token,
                    namePortfolioView.text.toString(),
                    descriptionPortfolioView.text.toString(),
                    image,
                    files
                )
                if (response == "ok") {
                    Toast.makeText(this@CreatePortfolioActivity, "Ok", Toast.LENGTH_LONG).show()
                    val resultIntent = Intent()
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this@CreatePortfolioActivity, response, Toast.LENGTH_LONG).show()

                }
            }
        } else {
            Toast.makeText(this@CreatePortfolioActivity, "Fill all fields", Toast.LENGTH_LONG)
                .show()
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
        filesPortfolioView.text = filenames
        return files
    }

    fun getFileNameFromUri(uri: Uri): String? {
        val returnCursor: Cursor = contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}