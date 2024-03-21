package com.example.collart.Projects

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.collart.Auth.CurrentUser
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.NetworkSystem.AllOrdersResponse
import com.example.collart.NetworkSystem.InteractionModule
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import com.example.collart.UserPage.UserPageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

class ProjectActivity : AppCompatActivity() {

    private lateinit var project: AllOrdersResponse

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
    private lateinit var clickUserView: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        val toolbar: Toolbar = findViewById(R.id.toolbarProjectPage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("")

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        project = intent.getSerializableExtra("project") as AllOrdersResponse

        projectSpecialistFind = findViewById(R.id.projectSpecialistFind)
        projectSpecialistFind.text = project.skill!!.nameRu

        clickUserView = findViewById(R.id.clickableUserFromProjectPage)
        clickUserView.setOnClickListener{
            onUserClick()
        }

        // TODO: add click to user profile

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
        var programs: String = "Программы: "
        project.tools.forEach{
            programs += it + ", "
        }
        if (project.tools.size == 0){
            programs += "-"
        }
        else{
            programs.dropLast(2)
        }
        programsMainProjectView.text = programs

        descriptionMainProjectView = findViewById(R.id.descriptionMainProjectView)
        descriptionMainProjectView.text = "О проекте: " + project.order.projectDescription

        dateProjectView = findViewById(R.id.dateProjectView)

        val date: String = getDateTime(project.order.dataStart) + "-" + getDateTime(project.order.dataEnd)
        dateProjectView.text = date

        joinProjectButton = findViewById(R.id.joinProjectButton)
        joinProjectButton.setOnClickListener {
            onJoinButtonClick()
        }
        // TODO: add click listener to join and write + add files placeholders
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

    fun onJoinButtonClick() {

        val userId: String = CurrentUser.user?.userData?.id.toString()
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

    private fun getDateTime(s: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val date = dateFormat.parse(s)
            val timestamp = date.time
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(timestamp)
            sdf.format(netDate)
        } catch (e: Exception) {
            s
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.project_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /*R.id.action_item1 -> {
                // Handle action item 1 click
                return true
            }
            R.id.action_item2 -> {
                // Handle action item 2 click
                return true
            }
            R.id.action_item3 -> {
                // Handle action item 3 click
                return true
            }
            else -> return super.onOptionsItemSelected(item)*/
        }
        return true
    }
}