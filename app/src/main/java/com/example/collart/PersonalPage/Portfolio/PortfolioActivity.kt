package com.example.collart.PersonalPage.Portfolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.collart.NetworkSystem.OrderResponse
import com.example.collart.NetworkSystem.Portfolio
import com.example.collart.R

class PortfolioActivity : AppCompatActivity() {

    private lateinit var portfolio: Portfolio

    private lateinit var titleView: TextView
    private lateinit var imageView: ImageView
    private lateinit var descriptionView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

        val toolbar: Toolbar = findViewById(R.id.toolbarPortfolio)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("")

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        portfolio = intent.getSerializableExtra("portfolio") as Portfolio

        titleView = findViewById(R.id.titleView)
        imageView = findViewById(R.id.portfolioImage)
    }
}