package com.example.collart.PersonalPage

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.collart.Auth.CurrentUser
import com.example.collart.Auth.User
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.NetworkSystem.SkillModule
import com.example.collart.NetworkSystem.ToolsModule
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import com.example.collart.Tools.UI.DropDownClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private var selectedPrograms = BooleanArray(1)
    private var programs = arrayOf("Программы")
    private val programsList = ArrayList<Int>()
    private var isSecondaryProfessionAdded = false
    private var skills: List<String> = listOf()

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerExperience: Spinner
    private lateinit var programsView: TextView
    private lateinit var radioGroupIsSearchable: RadioGroup
    private lateinit var radioButtonTrue: RadioButton
    private lateinit var radioButtonFalse: RadioButton
    private lateinit var etPassword: EditText
    private lateinit var tvAddSecondaryProfession: TextView
    private lateinit var firstProfessionView: TextView
    private lateinit var secondProfessionView: TextView
    private lateinit var etEmail: EditText
    private lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar: Toolbar = findViewById(R.id.editProfile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        etEmail = findViewById(R.id.emailEdit)
        etName = findViewById(R.id.nameEdit)
        etSurname = findViewById(R.id.surnameView)
        etDescription = findViewById(R.id.descriptionView)

        spinnerExperience = findViewById(R.id.experienceView)
        val states = Experience.entries.map { it.stringValue }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExperience.adapter = adapter

        radioGroupIsSearchable = findViewById(R.id.searchableGroup)
        radioButtonTrue = findViewById(R.id.searchTrueButton)
        radioButtonFalse = findViewById(R.id.searchFalseButton)

        etPassword = findViewById(R.id.etPassword)

        tvAddSecondaryProfession = findViewById(R.id.tvAddSecondaryProfession)
        firstProfessionView = findViewById(R.id.firstProfession)
        secondProfessionView = findViewById(R.id.secondProfession)

        GlobalScope.launch(Dispatchers.Main) {
            skills = SkillModule.getAllSkillsRu()
            val professionClickListener = DropDownClickListener(this@EditProfileActivity, skills) { selectedItem ->
                // Handle item selection here
                firstProfessionView.text = selectedItem
            }
            firstProfessionView.setOnClickListener(professionClickListener)
        }

        val professionClickListener = DropDownClickListener(this@EditProfileActivity, skills) { selectedItem ->
            // Handle item selection here
            firstProfessionView.text = selectedItem
        }
        firstProfessionView.setOnClickListener(professionClickListener)

        tvAddSecondaryProfession.setOnClickListener {
            if (!isSecondaryProfessionAdded) {
                addSecondaryProfessionView()
            }
        }

        initProgramsListView()

        updateButton = findViewById(R.id.updateUserBtn)
        updateButton.setOnClickListener {
            updateUser()
        }

        fillFields(CurrentUser.user)


    }

    private fun updateUser(){
        val user = CurrentUser.user

        var email: String? = null
        var name: String? = null
        var surname: String? = null
        var experience: String? = null
        var searchable: String? = null
        var description: String? = null
        var password: String? = null

        if (etEmail.text.toString().isNotEmpty() && etEmail.text.toString() != user.userData.email){
            email = etEmail.text.toString()
        }
        if (etName.text.toString().isNotEmpty() && etName.text.toString() != user.userData.name){
            name = etName.text.toString()
        }
        if (etSurname.text.toString().isNotEmpty() && etSurname.text.toString() != user.userData.surname){
            surname = etSurname.text.toString()
        }
        if (etDescription.text.toString().isNotEmpty() && etDescription.text.toString() != user.userData.description){
            description = etDescription.text.toString()
        }
        if (etPassword.text.toString().isNotEmpty()){
            password = etPassword.text.toString()
        }
        if (radioButtonTrue.isChecked != user.userData.searchable){
            searchable = if (radioButtonTrue.isChecked){
                "true"
            } else{
                "false"
            }
        }

        val experienceString = Experience.fromNameToString(spinnerExperience.selectedItem.toString())
        if (experienceString != user.userData.experience){
            experience = experienceString
        }

        var tools: MutableList<String>? = emptyList<String>().toMutableList()
        for (j in programsList.indices) {

            tools?.add(programs[programsList[j]])
        }

        var skills: MutableList<String>? = listOf(firstProfessionView.text.toString()).toMutableList()
        if (isSecondaryProfessionAdded){
            val secondSkill = secondProfessionView.text.toString()
            if (secondSkill != ""){
                skills?.add(secondSkill)
            }
        }

        var isSkillChange = false
        if (user.skills.isNotEmpty() && skills?.get(0) != user.skills[0].nameRu && skills?.get(0) != ""){
            isSkillChange = true
        }
        if (isSecondaryProfessionAdded && (user.skills.size < 2 || user.skills[1].nameRu != skills?.get(1))){
            isSkillChange = true
        }

        if (skills != null) {
            if (skills.size == 1 && skills[0] == ""){
                skills = null
            }
        }
        if (tools != null){
            if (tools.isEmpty()){
                tools = null
            }
        }

        if (email == null && name == null && surname == null
            && experience == null && searchable == null && description == null && firstProfessionView.text.toString() == "" && programsList.size == 0 && password == null
            && !isSkillChange && programsList.isEmpty()){
            Toast.makeText(this@EditProfileActivity, "No fields are changed", Toast.LENGTH_LONG).show()
            return
        }



        GlobalScope.launch((Dispatchers.Main)) {
            val response = UserModule.updateUser(CurrentUser.token, email, name, surname, searchable, experience, password, description, tools, skills, null, null)
            if (response == "ok"){
                Toast.makeText(this@EditProfileActivity, "Ok", Toast.LENGTH_LONG).show()
                val resultIntent = Intent()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            else{
                Toast.makeText(this@EditProfileActivity, response, Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun fillFields(user: User){
        etName.setText(user.userData.name)
        etSurname.setText(user.userData.surname)
        etDescription.setText(user.userData.description)
        etEmail.setText(user.userData.email)

        if (user.userData.searchable){
            radioButtonTrue.isChecked = true
        }
        else{
            radioButtonFalse.isChecked = true
        }


    }

    private fun getAvailablePrograms(){
        GlobalScope.launch(Dispatchers.Main) {
            val tools = ToolsModule.getAllTools(CurrentUser.token)
            programs = tools.map { tool -> tool.name }.toTypedArray()
            selectedPrograms = BooleanArray(programs.size)
        }
    }

    private fun initProgramsListView() {
        programsView = findViewById(R.id.programsView)
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

    private fun addSecondaryProfessionView() {

        tvAddSecondaryProfession.visibility = GONE
        secondProfessionView.visibility = VISIBLE
        secondProfessionView.isEnabled = true
        val professionClickListener = DropDownClickListener(this@EditProfileActivity, skills) { selectedItem ->
            // Handle item selection here
            secondProfessionView.text = selectedItem
        }
        secondProfessionView.setOnClickListener(professionClickListener)
        isSecondaryProfessionAdded = true
    }
}