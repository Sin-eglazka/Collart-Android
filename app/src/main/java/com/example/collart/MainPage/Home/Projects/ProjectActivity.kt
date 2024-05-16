package com.example.collart.MainPage.Home.Projects

import FileAdapter
import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.Auth.CurrentUser
import com.example.collart.Chat.Chat
import com.example.collart.Chat.ChatActivity
import com.example.collart.MainPage.Home.Specialists.UserPage.UserPageActivity
import com.example.collart.NetworkSystem.InteractionModule
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.NetworkSystem.OrderResponse
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import com.example.collart.Tools.FileConverter.FileConverter.Companion.extractFileNameFromUrl
import com.example.collart.Tools.TimeConverter.TimeConverter
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProjectActivity : AppCompatActivity(), FileAdapter.OnItemClickListener {
    private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1001

    private lateinit var project: OrderResponse
    private var urlFile: String = ""

    private lateinit var projectSpecialistFind: TextView
    private lateinit var authorImageView: ImageView
    private lateinit var authorNameView: TextView
    private lateinit var projectImage: ImageView
    private lateinit var projectNameView: TextView
    private lateinit var shortDescriptionProject: TextView
    private lateinit var experienceMainProjectView: TextView
    private lateinit var programsMainProjectView: TextView
    private lateinit var descriptionMainProjectView: TextView
    private lateinit var dateProjectView: TextView
    private lateinit var joinProjectButton: Button
    private lateinit var sendMessageBtn: Button
    private lateinit var clickUserView: LinearLayout
    private lateinit var recycleFiles: RecyclerView
    private lateinit var projectAction: ShapeableImageView
    private lateinit var btnAction: FrameLayout

    private var isLiked = false;

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Success download", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        val toolbar: Toolbar = findViewById(R.id.toolbarProjectPage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        project = intent.getSerializableExtra("project") as OrderResponse

        projectSpecialistFind = findViewById(R.id.projectSpecialistFind)
        projectSpecialistFind.text = project.skill!!.nameRu

        clickUserView = findViewById(R.id.clickableUserFromProjectPage)
        clickUserView.setOnClickListener{
            onUserClick()
        }

        val urlAvatar = project.order.owner.userPhoto.replace("http://", "https://")
        authorImageView = findViewById(R.id.authorImageView)
        Glide.with(this)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(authorImageView)

        authorNameView = findViewById(R.id.authorNameView)
        authorNameView.text = project.order.owner.name + " " + project.order.owner.surname

        val urlImage: String = project.order.image.replace("http://", "https://")
        projectImage = findViewById(R.id.projectImage)
        Glide.with(this)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(projectImage)

        projectNameView = findViewById(R.id.projectNameView)
        projectNameView.text = project.order.title

        shortDescriptionProject = findViewById(R.id.shortDescriptionProject)
        shortDescriptionProject.text = "Что требуется: " + project.order.taskDescription

        experienceMainProjectView = findViewById(R.id.experienceMainProjectView)
        experienceMainProjectView.text = "Опыт: " + Experience.fromString(project.order.experience).stringValue

        programsMainProjectView = findViewById(R.id.programsMainProjectView)
        var programs = "Программы: "
        project.tools.forEach{
            programs += "$it, "
        }
        if (project.tools.isEmpty()){
            programs += "-"
        }
        else{
            programs.dropLast(2)
        }
        programsMainProjectView.text = programs

        descriptionMainProjectView = findViewById(R.id.descriptionMainProjectView)
        descriptionMainProjectView.text = "О проекте: " + project.order.projectDescription

        dateProjectView = findViewById(R.id.dateProjectView)

        val date: String = TimeConverter.GetDateTime(project.order.dataStart) + "-" + TimeConverter.GetDateTime(project.order.dataEnd)
        dateProjectView.text = date

        joinProjectButton = findViewById(R.id.joinProjectButton)
        joinProjectButton.setOnClickListener {
            onJoinButtonClick()
        }

        sendMessageBtn = findViewById(R.id.sendMessageProjectBtn)
        sendMessageBtn.setOnClickListener {
            onSendMessageClick()
        }

        projectAction = findViewById(R.id.buttonActionProject)
        btnAction = findViewById(R.id.buttonActionProjectContainer)

        if (!project.order.isActive || project.order.owner.id == CurrentUser.user.userData.id){
            joinProjectButton.visibility = GONE
            sendMessageBtn.visibility = GONE
        }

        if (project.order.isActive && project.order.owner.id == CurrentUser.user.userData.id){
            projectAction.setImageResource(R.drawable.delete)
            btnAction.setOnClickListener {
                deleteAction()
            }
        }
        else{
            projectAction.setImageResource(R.drawable.not_favorite)
            checkFavorite()
            btnAction.setOnClickListener{
                likeAction()
            }
        }

        recycleFiles = findViewById(R.id.recycleFileView)
        recycleFiles.layoutManager = LinearLayoutManager(this)
        val adapter = FileAdapter(project.order.files)
        adapter.setOnItemClickListener(this)
        recycleFiles.adapter = adapter

        registerReceiver(
            downloadCompleteReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private fun likeAction(){
        GlobalScope.launch(Dispatchers.Main) {
            if (isLiked){
                val message = OrderModule.removeOrderFromFavorite(CurrentUser.token, project.order.id)
                if (message != "ok"){
                    Toast.makeText(this@ProjectActivity, message, Toast.LENGTH_SHORT).show()
                }
                else{
                    isLiked = false
                }
            }
            else{
                val message = OrderModule.addOrderToFavorite(CurrentUser.token, project.order.id)
                if (message != "ok"){
                    Toast.makeText(this@ProjectActivity, message, Toast.LENGTH_SHORT).show()
                }
                else{
                    isLiked = true
                }
            }
            isLiked = OrderModule.isOrderFavorite(CurrentUser.token, project.order.id)
            if (isLiked){
                projectAction.setImageResource(R.drawable.favorite)
            }
            else{
                projectAction.setImageResource(R.drawable.not_favorite)
            }
        }
    }

    private fun deleteAction(){
        GlobalScope.launch(Dispatchers.Main) {
            val message = OrderModule.deleteOrder(CurrentUser.token, project.order.id)
            if (message == "ok"){
                finish()
            }
        }
    }

    private fun checkFavorite(){
        GlobalScope.launch(Dispatchers.Main) {
            isLiked = OrderModule.isOrderFavorite(CurrentUser.token, project.order.id)
            if (isLiked){
                projectAction.setImageResource(R.drawable.favorite)
            }
            else{
                projectAction.setImageResource(R.drawable.not_favorite)
            }
        }
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
                downloadFile(urlFile, extractFileNameFromUrl(urlFile))
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
                    downloadFile(urlFile, extractFileNameFromUrl(urlFile))
                }
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        urlFile = ""
    }
    private fun onSendMessageClick(){
        GlobalScope.launch(Dispatchers.Main) {

            val userResponse = UserModule.getSpecialist(CurrentUser.token, project.order.owner.id)
            if (userResponse == null) {
                Toast.makeText(this@ProjectActivity, "Error on server", Toast.LENGTH_LONG).show()
                return@launch
            }

            val chat = Chat(false, "", userResponse.userData.userPhoto, userResponse.userData.id, "", 0, userResponse.userData.name + " " + userResponse.userData.surname)
            val intent = Intent(this@ProjectActivity, ChatActivity::class.java)

            intent.putExtra("Chat", chat)

            startActivity(intent)
        }
    }

    private fun onUserClick(){
        GlobalScope.launch(Dispatchers.Main) {

            val userResponse = UserModule.getSpecialist(CurrentUser.token, project.order.owner.id)
            if (userResponse == null){
                Toast.makeText(this@ProjectActivity, "Error on server", Toast.LENGTH_LONG).show()
                return@launch
            }

            val intent = Intent(this@ProjectActivity, UserPageActivity::class.java)

            intent.putExtra("Specialist", userResponse)

            startActivity(intent)
        }
    }

    private fun onJoinButtonClick() {

        val userId: String = CurrentUser.user.userData.id
        GlobalScope.launch(Dispatchers.Main) {
            val response = InteractionModule.createInteraction(
                CurrentUser.token,
                userId,
                project.order.owner.id,
                project.order.id
            )
            if (response == "ok"){
                Toast.makeText(this@ProjectActivity, "Request send", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this@ProjectActivity, response, Toast.LENGTH_LONG).show()
            }
        }
    }
}