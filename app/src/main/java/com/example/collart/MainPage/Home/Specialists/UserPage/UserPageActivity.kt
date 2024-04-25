package com.example.collart.MainPage.Home.Specialists.UserPage

import ActiveProjectsAdapter
import ProjectType
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.Auth.CurrentUser
import com.example.collart.Auth.User
import com.example.collart.Chat.Chat
import com.example.collart.Chat.ChatActivity
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.NetworkSystem.InteractionModule
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.NetworkSystem.Portfolio
import com.example.collart.NetworkSystem.PortfolioModule
import com.example.collart.PersonalPage.Portfolio.PortfolioViewAdapter
import com.example.collart.MainPage.Home.Projects.ProjectActivity
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserPageActivity : AppCompatActivity(), ActiveProjectsAdapter.OnItemClickListener, PortfolioViewAdapter.OnItemClickListener {

    private lateinit var specialist: User
    private var myProjects: MutableList<Project> = emptyList<Project>().toMutableList()

    private lateinit var inviteUserBtn: Button
    private lateinit var messageBtn: Button
    private lateinit var avatarUserView: ImageView
    private lateinit var backgroundUserView: ImageView
    private lateinit var usernameView: TextView
    private lateinit var userProfession: TextView
    private lateinit var userEmailView: TextView
    private lateinit var projectsRecycleView: RecyclerView
    private lateinit var toolbarButton: List<Button>

    private var activeProjects: MutableList<Project> = emptyList<Project>().toMutableList()
    private var collaborationProjects: MutableList<Project> = emptyList<Project>().toMutableList()
    private var portfolios: MutableList<Portfolio> = emptyList<Portfolio>().toMutableList()

    private lateinit var activeProjectsAdapter: ActiveProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        val toolbar: Toolbar = findViewById(R.id.toolbarUserPage)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle("")

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        specialist = intent.getSerializableExtra("Specialist") as User

        inviteUserBtn = findViewById(R.id.inviteUserBtn)
        inviteUserBtn.setOnClickListener {
            onInviteClick()
        }

        messageBtn = findViewById(R.id.messageBtn)
        messageBtn.setOnClickListener {
            onSendMessageClick()
        }

        avatarUserView = findViewById(R.id.avatarUserView)
        backgroundUserView = findViewById(R.id.backgroundUserView)
        usernameView = findViewById(R.id.userNameView)
        userProfession = findViewById(R.id.userProfessionView)
        userEmailView = findViewById(R.id.userEmailView)

        val urlImage: String = specialist.userData.cover.replace("http://", "https://")!!
        val urlAvatar = specialist.userData.userPhoto.replace("http://", "https://")!!

        Glide.with(this)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(backgroundUserView)

        Glide.with(this)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(avatarUserView)

        usernameView.text = specialist.userData.name + " " + specialist.userData.surname

        var skillsText: String = ""

        specialist.skills.forEach{
            skillsText += it.nameRu + ", "
        }
        if (specialist.skills.size > 0){
            skillsText = skillsText.dropLast(2)
        }
        userProfession.text = skillsText


        if (specialist.userData.searchable) {
            userEmailView.text = specialist.userData.email
        }
        else{
            userEmailView.text = ""
        }

        projectsRecycleView = findViewById(R.id.projectsView)
        projectsRecycleView.layoutManager = LinearLayoutManager(this)

        uploadPortfolioProjects()

        toolbarButton = listOf(
            findViewById(R.id.userActiveBtn),
            findViewById(R.id.userPortfolioBtn),
            findViewById(R.id.userCollaborationBtn))

        val inactiveTextColor = ContextCompat.getColor(this, R.color.black)
        val activeTextColor = ContextCompat.getColor(this, R.color.purple)


        val drawable = ContextCompat.getDrawable(this, R.drawable.menu_button)
        toolbarButton[1].setTextColor(activeTextColor)
        toolbarButton[1].background = drawable

        for (button in toolbarButton){
            button.setOnClickListener{
                toolbarButton.forEach{
                    it.setTextColor(inactiveTextColor)
                    it.background = null
                    it.backgroundTintList = null
                }
                button.setTextColor(activeTextColor)
                button.background = drawable

                when (button.id) {
                    R.id.userActiveBtn -> {
                        activeProjectsAdapter = ActiveProjectsAdapter(
                            this,
                            activeProjects,
                            ProjectType.ActiveProject
                        )
                        activeProjectsAdapter.setOnItemClickListener(this@UserPageActivity)
                        projectsRecycleView.layoutManager = LinearLayoutManager(this)
                        projectsRecycleView.adapter = activeProjectsAdapter
                        uploadActiveProjects()
                    }
                    R.id.userPortfolioBtn -> {
                        val adapter = PortfolioViewAdapter(this, portfolios)
                        adapter.setOnItemClickListener(this)
                        projectsRecycleView.layoutManager = GridLayoutManager(this, 2)
                        projectsRecycleView.adapter = adapter
                        uploadPortfolioProjects()
                    }
                    R.id.userCollaborationBtn -> {
                        activeProjectsAdapter = ActiveProjectsAdapter(
                            this,
                            collaborationProjects,
                            ProjectType.CollaborationProject
                        )
                        activeProjectsAdapter.setOnItemClickListener(this)
                        projectsRecycleView.layoutManager = LinearLayoutManager(this)
                        projectsRecycleView.adapter = activeProjectsAdapter
                        uploadCollaborationProjects()
                    }
                }
            }
        }
    }

    private fun onSendMessageClick(){
            val chat = Chat(false, "", specialist.userData.userPhoto, specialist.userData.id, "", 0, specialist.userData.name + " " + specialist.userData.surname)
            val intent = Intent(this@UserPageActivity, ChatActivity::class.java)

            intent.putExtra("Chat", chat)

            startActivity(intent)
    }

    private fun uploadActiveProjects() {
        GlobalScope.launch(Dispatchers.Main) {
            activeProjects =
                OrderModule.getMyOrders(CurrentUser.token, specialist.userData.id)
                    .toMutableList()
                activeProjectsAdapter = ActiveProjectsAdapter(
                    this@UserPageActivity,
                    activeProjects,
                    ProjectType.ActiveProject
                )
                activeProjectsAdapter.setOnItemClickListener(this@UserPageActivity)
                projectsRecycleView.layoutManager = LinearLayoutManager(this@UserPageActivity)
                projectsRecycleView.adapter = activeProjectsAdapter
        }
    }

    private fun uploadCollaborationProjects(){
        GlobalScope.launch(Dispatchers.Main) {
            collaborationProjects = OrderModule.getMyCollaborationOrders(CurrentUser.token, specialist.userData.id).toMutableList()

                activeProjectsAdapter = ActiveProjectsAdapter(
                    this@UserPageActivity,
                    collaborationProjects,
                    ProjectType.CollaborationProject
                )
                activeProjectsAdapter.setOnItemClickListener(this@UserPageActivity)
                projectsRecycleView.layoutManager = LinearLayoutManager(this@UserPageActivity)
                projectsRecycleView.adapter = activeProjectsAdapter
            }

    }

    private fun uploadPortfolioProjects(){
        GlobalScope.launch(Dispatchers.Main) {
            portfolios = PortfolioModule.getMyPortfolios(CurrentUser.token, specialist.userData.id).toMutableList()

                val adapter = PortfolioViewAdapter(
                    this@UserPageActivity,
                    portfolios
                )
                adapter.setOnItemClickListener(this@UserPageActivity)
                projectsRecycleView.layoutManager = GridLayoutManager(this@UserPageActivity, 2)
                projectsRecycleView.adapter = adapter

        }
    }

    override fun onItemClick(position: Int, type: ProjectType) {
        val projectId: String
        when(type){
            ProjectType.ActiveProject -> {
                if (position < activeProjects.size){
                    projectId = activeProjects[position].id
                }
                else{
                    return
                }
            }
            ProjectType.CollaborationProject -> {
                if (position < collaborationProjects.size){
                    projectId = collaborationProjects[position].id
                }
                else{
                    return
                }
            }
            else -> return
        }
        GlobalScope.launch(Dispatchers.Main) {

            val projectResponse = OrderModule.getOrder(CurrentUser.token, projectId)
            if (projectResponse == null){
                Toast.makeText(this@UserPageActivity, "Error on server", Toast.LENGTH_LONG).show()
                return@launch
            }

            val intent = Intent(this@UserPageActivity, ProjectActivity::class.java)

            intent.putExtra("project", projectResponse)

            startActivity(intent)
        }
    }

    override fun onItemClick(position: Int) {
        // TODO: open portfolio activity
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)

        val menuItem = menu?.findItem(R.id.block)
        // Change the text color of the menu item
        // Create a SpannableString to set the text color
        val spannable = SpannableString(menuItem?.title)
        spannable.setSpan(
            ForegroundColorSpan(Color.RED), // Set your desired color
            0, // Start index of the span
            spannable.length, // End index of the span (length of the text)
            0 // No flags
        )

        // Set the modified SpannableString back to the menu item
        menuItem?.title = spannable
        return true
    }

    fun onInviteClick() {
        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.choose_project_sheet, null)

        val recycleMyProjects = view.findViewById<RecyclerView>(R.id.myProjectsView)
        recycleMyProjects.layoutManager = LinearLayoutManager(this)
        var adapter = ActiveProjectsAdapter(this, myProjects, ProjectType.ChooseActiveProject)
        recycleMyProjects.adapter = adapter

        GlobalScope.launch(Dispatchers.Main) {
            myProjects = OrderModule.getMyOrders(CurrentUser.token, CurrentUser.user.userData.id).toMutableList()
            adapter = ActiveProjectsAdapter(this@UserPageActivity, myProjects, ProjectType.ChooseActiveProject)
            recycleMyProjects.adapter = adapter
        }

        val btnAdd = view.findViewById<Button>(R.id.addButton)
        btnAdd.setOnClickListener {
            if (adapter.selectedItemPosition != RecyclerView.NO_POSITION){
                val index = adapter.selectedItemPosition
                if (index < myProjects.size){
                    val userId: String = CurrentUser.user?.userData?.id.toString()
                    GlobalScope.launch(Dispatchers.Main) {
                        val response = InteractionModule.createInteraction(
                            CurrentUser.token,
                            userId,
                            specialist.userData.id,
                            myProjects[index].id
                        )
                        if (response == "ok"){
                            Toast.makeText(this@UserPageActivity, "Invite send", Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(this@UserPageActivity, response, Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }
            }
            else{
                Toast.makeText(this, "Choose one project", Toast.LENGTH_LONG).show()
            }
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
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