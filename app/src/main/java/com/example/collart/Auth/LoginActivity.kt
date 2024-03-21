package com.example.collart.Auth

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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
    private lateinit var googleButton: ImageButton
    private lateinit var facebookButton: ImageButton
    private lateinit var appleIdButton: ImageButton
    private lateinit var forgetPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.collart.R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        // Find views
        forgetPasswordTextView = findViewById(R.id.forgetPasswordTextView)
        googleButton = findViewById(R.id.googleBtn)
        facebookButton = findViewById(R.id.facebookBtn)
        appleIdButton = findViewById(R.id.appleBtn)
        loginEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        authButton = findViewById(R.id.authButton)
        loginEditText.addTextChangedListener (textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        supportActionBar?.title = "Вход"
        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this@LoginActivity, com.example.collart.R.color.white)))
            setHomeAsUpIndicator(com.example.collart.R.drawable.baseline_arrow_back_24)

        }

        // Set click listeners for "Forget password?" and "Create new account" text
        forgetPasswordTextView.setOnClickListener {
            // Handle "Forget password?" click
            // Add your logic to navigate to the forgot password screen
        }

        // Set click listeners for social login buttons
        googleButton.setOnClickListener {
            // Handle Google button click
            // Add your logic for Google login
        }

        facebookButton.setOnClickListener {
            // Handle Facebook button click
            // Add your logic for Facebook login
        }

        appleIdButton.setOnClickListener {
            // Handle Apple ID button click
            // Add your logic for Apple ID login
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
            val isLoginFilled: Boolean = loginEditText.text.length > 0
            val isPasswordFilled: Boolean = passwordEditText.text.length > 0
            authButton.setEnabled(isLoginFilled && isPasswordFilled)
            authButton.setBackgroundResource(if (isLoginFilled && isPasswordFilled) com.example.collart.R.drawable.primary_button else com.example.collart.R.drawable.inactive_button)
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