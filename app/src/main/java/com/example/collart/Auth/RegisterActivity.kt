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
import com.example.collart.Tools.UI.DropDownClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etMail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var secondaryProfessionContainer: LinearLayout
    private lateinit var btnRegister: Button
    private lateinit var tvError: TextView
    private lateinit var tvAddSecondaryProfession: TextView
    private lateinit var firstProfessionView: TextView
    private lateinit var secondProfessionView: TextView

    lateinit var editTexts:List<EditText>

    private var isSecondaryProfessionAdded = false
    private var skills: List<String> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = "Регистрация"
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@RegisterActivity, R.color.white)))
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        }


        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etMail = findViewById(R.id.etMail)
        etPassword = findViewById(R.id.etPassword)
        etRepeatPassword = findViewById(R.id.etRepeatPassword)
        secondaryProfessionContainer = findViewById(R.id.container)
        btnRegister = findViewById(R.id.btnRegister)
        tvError = findViewById(R.id.tvError)
        tvAddSecondaryProfession = findViewById(R.id.tvAddSecondaryProfession)
        firstProfessionView = findViewById(R.id.firstProfession)
        secondProfessionView = findViewById(R.id.secondProfession)

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
            etSurname,
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
            val backgroundDrawable = if ((s?.length ?: 0) > 0) {
                R.drawable.secondary_button
            } else {
                R.drawable.inactive_edittext
            }


            var isAllFilled = true
            editTexts.forEach{
                if (it.text.isNotEmpty()){
                    it.setBackgroundResource(backgroundDrawable)
                }
                else{
                    isAllFilled = false
                }
            }
            if (firstProfessionView.text.isNotEmpty()){
                firstProfessionView.setBackgroundResource(backgroundDrawable)
            }
            else{
                isAllFilled = false
            }

            if (isSecondaryProfessionAdded){
                if (secondProfessionView.text.isNotEmpty()){
                    secondProfessionView.setBackgroundResource(backgroundDrawable)
                }
                else{
                    isAllFilled = false
                }
            }
            btnRegister.isEnabled = isAllFilled
            btnRegister.setBackgroundResource(if (isAllFilled) R.drawable.primary_button else R.drawable.inactive_button)
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

            val name: String = etName.text.toString()
            val surname: String = etSurname.text.toString()


            GlobalScope.launch(Dispatchers.Main) {
                if (!NetworkClient.isNetworkAvailable(this@RegisterActivity)){
                    Toast.makeText(this@RegisterActivity, "No internet connection", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (!NetworkClient.isDomainAvailable()){
                    Toast.makeText(this@RegisterActivity, "Server unavailable", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val first = firstProfessionView.text.toString()
                val sec = secondProfessionView.text.toString()
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
        secondProfessionView.isEnabled = true
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