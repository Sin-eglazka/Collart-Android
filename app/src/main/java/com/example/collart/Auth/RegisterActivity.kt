package com.example.collart.Auth


import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.AdapterView.GONE
import android.widget.AdapterView.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.NetworkSystem.NetworkClient
import com.example.collart.NetworkSystem.SkillModule
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import com.example.collart.ui.DropDownClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etLogin: EditText
    private lateinit var etMail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var secondaryProfessionContainer: LinearLayout
    private lateinit var btnRegister: Button
    private lateinit var tvError: TextView
    private lateinit var tvAddSecondaryProfession: TextView
    private lateinit var firstProfessionView: TextView
    private lateinit var secondProfessionView: TextView

    lateinit var editTexts:List<EditText>;

    private var isSecondaryProfessionAdded = false
    private var skills: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.collart.R.layout.activity_register)

        supportActionBar?.title = "Регистрация"
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@RegisterActivity, com.example.collart.R.color.white)))
            setHomeAsUpIndicator(com.example.collart.R.drawable.baseline_arrow_back_24)

        }


        etName = findViewById(com.example.collart.R.id.etName)
        etLogin = findViewById(com.example.collart.R.id.etLogin)
        etMail = findViewById(com.example.collart.R.id.etMail)
        etPassword = findViewById(com.example.collart.R.id.etPassword)
        etRepeatPassword = findViewById(com.example.collart.R.id.etRepeatPassword)
        secondaryProfessionContainer = findViewById(com.example.collart.R.id.container)
        btnRegister = findViewById(com.example.collart.R.id.btnRegister)
        tvError = findViewById(com.example.collart.R.id.tvError)
        tvAddSecondaryProfession = findViewById(com.example.collart.R.id.tvAddSecondaryProfession)
        firstProfessionView = findViewById(com.example.collart.R.id.firstProfession)
        secondProfessionView = findViewById(com.example.collart.R.id.secondProfession)

        GlobalScope.launch(Dispatchers.Main) {
            skills = SkillModule.getAllSkillsRu()
            val professionClickListener = DropDownClickListener(this@RegisterActivity, skills) { selectedItem ->
                // Handle item selection here
                firstProfessionView.text = selectedItem
            }
            firstProfessionView.setOnClickListener(professionClickListener)
        }

        val professionClickListener = DropDownClickListener(this@RegisterActivity, skills) { selectedItem ->
            // Handle item selection here
            firstProfessionView.text = selectedItem
        }
        firstProfessionView.setOnClickListener(professionClickListener)

        editTexts = listOf(
            etName,
            etLogin,
            etMail,
            etPassword,
            etRepeatPassword
        )

        editTexts.forEach {
            it.addTextChangedListener(textWatcher)
        }
        firstProfessionView.addTextChangedListener(textWatcher)



        // Register button click listener
        btnRegister.setOnClickListener {
            register()
        }

        // Add secondary profession click listener
        tvAddSecondaryProfession.setOnClickListener {
            if (!isSecondaryProfessionAdded) {
                addSecondaryProfessionView()
            }
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val backgroundDrawable = if (s?.length ?: 0 > 0) {
                resources.getDrawable(R.drawable.secondary_button, null)
            } else {
                resources.getDrawable(R.drawable.inactive_edittext, null)
            }


            var isAllFilled = true
            editTexts.forEach{
                if (it.text.isNotEmpty()){
                    it.background = backgroundDrawable
                }
                else{
                    isAllFilled = false
                }
            }
            if (firstProfessionView.text.isNotEmpty()){
                firstProfessionView.background = backgroundDrawable
            }
            else{
                isAllFilled = false
            }

            if (isSecondaryProfessionAdded){
                if (secondProfessionView.text.isNotEmpty()){
                    secondProfessionView.background = backgroundDrawable
                }
                else{
                    isAllFilled = false
                }
            }
            btnRegister.setEnabled(isAllFilled)
            btnRegister.setBackgroundResource(if (isAllFilled) com.example.collart.R.drawable.primary_button else com.example.collart.R.drawable.inactive_button)
        }
    }

    private fun register() {
        var messageError = ""

        // Validate passwords
        val password = etPassword.text.toString()
        val repeatPassword = etRepeatPassword.text.toString()
        if (password != repeatPassword) {
            messageError += "Пароли не совпадают\n"
        }

        if (isSecondaryProfessionAdded && (firstProfessionView.text.toString() == secondProfessionView.text.toString())){
            messageError += "Специальности не должны совпадать\n"
        }

        if (messageError.isNotEmpty()){
            tvError.visibility = VISIBLE
            tvError.text = messageError
        }
        else{
            tvError.visibility = GONE
            tvError.text = messageError


            val parts: List<String> =  etName.text.toString().trim().split(" ")

            var name: String = ""
            var surname: String = ""
            if (parts.size >= 2){
                name = parts[0]
                surname = parts[1]
            }
            else{
                name = etName.text.toString()
            }
            // TODO: Perform registration logic here
            //TODO: Get request from server

            GlobalScope.launch(Dispatchers.Main) {
                if (!NetworkClient.isNetworkAvailable(this@RegisterActivity)){
                    Toast.makeText(this@RegisterActivity, "No internet connection", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (!NetworkClient.isDomainAvailable()){
                    Toast.makeText(this@RegisterActivity, "Server unavailable", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                var first = firstProfessionView.text.toString()
                var sec = secondProfessionView.text.toString()
                var userSkills: List<String> = listOf(first)
                if (isSecondaryProfessionAdded){
                    userSkills = listOf(first, sec)
                }

                val response = UserModule.registerUser(
                    etMail.text.toString(),
                    etPassword.text.toString(),
                    etRepeatPassword.text.toString(),
                    name,
                    surname,
                    "",
                    "",
                    "",
                    true,
                    Experience.NO_EXPERIENCE,
                    userSkills,
                    listOf())
                if (response == "ok") {

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, response, Toast.LENGTH_SHORT).show()

                }
            }

        }


    }

    private fun addSecondaryProfessionView() {

        tvAddSecondaryProfession.visibility = GONE
        secondProfessionView.visibility = VISIBLE
        secondProfessionView.setEnabled(true)
        val professionClickListener = DropDownClickListener(this@RegisterActivity, skills) { selectedItem ->
            // Handle item selection here
            secondProfessionView.text = selectedItem
        }
        secondProfessionView.setOnClickListener(professionClickListener)
        secondProfessionView.addTextChangedListener(textWatcher)
        isSecondaryProfessionAdded = true
    }

    // Handle the up button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}