package com.example.collart.UserPage

import ActiveProjectsAdapter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.Auth.CurrentUser
import com.example.collart.Auth.User
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.NetworkSystem.InteractionModule
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserPageActivity : AppCompatActivity() {

    private lateinit var specialist: User
    private var myProjects: MutableList<Project> = emptyList<Project>().toMutableList()

    private lateinit var inviteUserBtn: Button
    private lateinit var avatarUserView: ImageView
    private lateinit var backgroundUserView: ImageView
    private lateinit var usernameView: TextView
    private lateinit var userProfession: TextView
    private lateinit var userEmailView: TextView

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
            myProjects = OrderModule.getMyOrders(CurrentUser.token).toMutableList()
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