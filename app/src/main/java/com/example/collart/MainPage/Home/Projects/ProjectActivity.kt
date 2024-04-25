package com.example.collart.MainPage.Home.Projects

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.example.collart.Tools.TimeConverter.TimeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProjectActivity : AppCompatActivity() {

    private lateinit var project: OrderResponse

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
        // TODO: add click listener write + add files placeholders
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.project_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.hide -> {
                // Handle action item 1 click
                return true
            }
            R.id.share -> {
                // Handle action item 2 click
                return true
            }
            R.id.like -> {
                GlobalScope.launch(Dispatchers.Main) {
                    val response = OrderModule.addOrderToFavorite(CurrentUser.token, project.order.id)
                    if (response != "ok"){
                        Toast.makeText(this@ProjectActivity, response, Toast.LENGTH_LONG).show()
                        return@launch
                    }
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}