package com.example.collart

import TokenManager
import com.example.collart.OnBoarding.OnboardingActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.collart.Auth.CurrentUser
import com.example.collart.Auth.LoginActivity
import com.example.collart.Auth.RegisterActivity
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.MainPage.MainPageActivity
import com.example.collart.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    companion object {
        var currentInput = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = TokenManager.getToken(this@MainActivity)

        if (!token.isNullOrBlank()){
            GlobalScope.launch(Dispatchers.Main) {
                CurrentUser.user = UserModule.getCurrentUser(token)
                CurrentUser.token = token
                if (CurrentUser.user != null){
                    val intent = Intent(this@MainActivity, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        if (currentInput == 0) {
            ++currentInput
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


}