package com.example.collart.PersonalPage.Portfolio

import FileAdapter
import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.NetworkSystem.OrderResponse
import com.example.collart.NetworkSystem.Portfolio
import com.example.collart.R
import com.example.collart.Tools.FileConverter.FileConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortfolioActivity : AppCompatActivity(), FileAdapter.OnItemClickListener {

    private lateinit var portfolio: Portfolio
    private var urlFile: String = ""
    private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 101

    private lateinit var titleView: TextView
    private lateinit var imageView: ImageView
    private lateinit var descriptionView: TextView
    private lateinit var recycleFiles: RecyclerView

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Success download", Toast.LENGTH_SHORT).show()
        }
    }

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
        titleView.setText(portfolio.name)

        imageView = findViewById(R.id.portfolioImage)
        val urlImage: String = portfolio.image.replace("http://", "https://")
        Glide.with(this)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(imageView)

        descriptionView = findViewById(R.id.descriptionPortfolioView)
        descriptionView.setText(portfolio.description)

        recycleFiles = findViewById(R.id.recycleFileView)
        recycleFiles.layoutManager = LinearLayoutManager(this)
        val adapter = FileAdapter(portfolio.files)
        adapter.setOnItemClickListener(this)
        recycleFiles.adapter = adapter

        registerReceiver(
            downloadCompleteReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister broadcast receiver
        unregisterReceiver(downloadCompleteReceiver)
    }

    override fun onFileClick(url: String){
        urlFile = url.replace("http://", "https://")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION
            )
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                downloadFile(urlFile, FileConverter.extractFileNameFromUrl(urlFile))
                urlFile = ""
            }
        }

    }

    private fun downloadFile(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading $fileName")
            .setDescription("Download in progress")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GlobalScope.launch(Dispatchers.Main) {
                    downloadFile(urlFile, FileConverter.extractFileNameFromUrl(urlFile))
                }
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        urlFile = ""
    }
}