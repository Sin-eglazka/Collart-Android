package com.example.collart.Auth

import TokenManager
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.collart.MainPage.MainPageActivity
import com.example.collart.NetworkSystem.NetworkClient
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import com.example.collart.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var authButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Find views
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        authButton = findViewById(R.id.authButton)
        loginEditText.addTextChangedListener (textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        supportActionBar?.title = "Вход"
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@LoginActivity, R.color.white)))
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        }


        authButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (!NetworkClient.isNetworkAvailable(this@LoginActivity)){
                    Toast.makeText(this@LoginActivity, "No internet connection", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if (!NetworkClient.isDomainAvailable()){
                    Toast.makeText(this@LoginActivity, "Server unavailable", Toast.LENGTH_SHORT).show()
                    return@launch
                }


                val token = UserModule.loginUser(loginEditText.text.toString(), passwordEditText.text.toString())
                if (token != null) {
                    TokenManager.saveToken(this@LoginActivity, token)
                    CurrentUser.token = token
                    CurrentUser.user = UserModule.getCurrentUser(token)
                    val intent = Intent(this@LoginActivity, MainPageActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Incorrect email or password", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Check if both fields are filled to enable auth button
            val isLoginFilled: Boolean = loginEditText.text.isNotEmpty()
            val isPasswordFilled: Boolean = passwordEditText.text.isNotEmpty()
            authButton.isEnabled = isLoginFilled && isPasswordFilled
            authButton.setBackgroundResource(if (isLoginFilled && isPasswordFilled) R.drawable.primary_button else R.drawable.inactive_button)
        }
        override fun afterTextChanged(s: Editable?) {}
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