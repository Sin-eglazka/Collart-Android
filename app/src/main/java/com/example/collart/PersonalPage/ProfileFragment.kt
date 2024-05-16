package com.example.collart.PersonalPage

import ActiveProjectsAdapter
import ProjectType
import TokenManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.Auth.CurrentUser.token
import com.example.collart.Auth.CurrentUser.user
import com.example.collart.Auth.User
import com.example.collart.Auth.UserData
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.MainPage.Home.Projects.ProjectActivity
import com.example.collart.MainPage.MainPageActivity
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.NetworkSystem.Portfolio
import com.example.collart.NetworkSystem.PortfolioModule
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.OnBoarding.MainActivity
import com.example.collart.PersonalPage.CreateOrder.CreateOrderActivity
import com.example.collart.PersonalPage.Portfolio.CreatePortfolioActivity
import com.example.collart.PersonalPage.Portfolio.PortfolioActivity
import com.example.collart.PersonalPage.Portfolio.PortfolioViewAdapter
import com.example.collart.R
import com.example.collart.Tools.FileConverter.FileConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ProfileFragment : Fragment(), ActiveProjectsAdapter.OnItemClickListener, PortfolioViewAdapter.OnItemClickListener {

    private lateinit var avatarUserView: ImageView
    private lateinit var backgroundUserView: ImageView
    private lateinit var usernameView: TextView
    private lateinit var userProfession: TextView
    private lateinit var userEmailView: TextView
    private lateinit var createOrderBtn: Button
    private lateinit var toolbarButton: List<Button>
    private lateinit var addPortfolioBtn: Button
    private lateinit var projectsRecycleView: RecyclerView

    private var activeProjects: MutableList<Project> = emptyList<Project>().toMutableList()
    private var collaborationProjects: MutableList<Project> = emptyList<Project>().toMutableList()
    private var favoriteProjects: MutableList<Project> = emptyList<Project>().toMutableList()
    private var portfolios: MutableList<Portfolio> = emptyList<Portfolio>().toMutableList()

    private lateinit var activeProjectsAdapter: ActiveProjectsAdapter

    private var image: File? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        image = if (uri != null) {
            val path = FileConverter.getImagePathFromInputStreamUri(uri, requireContext(), "cover.jpg")
            path?.let { File(it) }
        } else{
            null
        }
    }

    private val startChildActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uploadPortfolioProjects()
            }
        }

    private val editActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                if (isAdded && context != null) {
                    user = UserModule.getCurrentUser(token)
                    fillViews()
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal_page, container, false)

        return view
    }

    private fun fillViews(){
        val urlImage: String = user.userData.cover.replace("http://", "https://")!!
        val urlAvatar = user.userData.userPhoto.replace("http://", "https://")!!

        Glide.with(requireContext())
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(backgroundUserView)

        Glide.with(requireContext())
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(avatarUserView)

        usernameView.text = user.userData.name + " " + user.userData.surname

        var skillsText = ""

        user.skills.forEach{
            skillsText += it.nameRu + ", "
        }
        if (skillsText.length > 0){
            skillsText = skillsText.dropLast(2)
        }
        userProfession.text = skillsText


        if (user.userData.searchable) {
            userEmailView.text = user.userData.email
        }
        else{
            userEmailView.text = ""
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        avatarUserView = view.findViewById(R.id.avatarUserView)
        backgroundUserView = view.findViewById(R.id.backgroundUserView)
        usernameView = view.findViewById(R.id.userNameView)
        userProfession = view.findViewById(R.id.userProfessionView)
        userEmailView = view.findViewById(R.id.userEmailView)
        createOrderBtn = view.findViewById(R.id.createProjectBtn)
        addPortfolioBtn = view.findViewById(R.id.addPortfolioBtn)
        projectsRecycleView = view.findViewById(R.id.projectsView)
        projectsRecycleView.layoutManager = LinearLayoutManager(context)

        uploadActiveProjects()

        if (user == null){
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                if (isAdded && context != null) {
                    user = UserModule.getCurrentUser(token)
                    if (user == null) {
                        Toast.makeText(
                            requireContext(),
                            "Incorrect user credentials",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(requireContext(), MainPageActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    fillViews()
                }
            }
        }
        else{
            fillViews()
        }



        addPortfolioBtn.setOnClickListener {

            val intent = Intent(context, CreatePortfolioActivity::class.java)

            startChildActivityForResult.launch(intent)
        }

        createOrderBtn.setOnClickListener{
            val context: Context? = activity

            val intent = Intent(context, CreateOrderActivity::class.java)

            context?.startActivity(intent)
        }

        val toolbar: Toolbar = view.findViewById(R.id.toolbarPersonalUserPage)

        toolbar.title = ""
        toolbar.inflateMenu(R.menu.personal_user_menu)
        val menu = toolbar.menu
        val menuItem = menu?.findItem(R.id.logout)
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


        // TODO: add click listneners
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.editProfile -> {
                    val context: Context? = activity

                    val intent = Intent(context, EditProfileActivity::class.java)

                    editActivityResultLauncher.launch(intent)
                    //context?.startActivity(intent)

                    true
                }
                R.id.editBackground -> {
                    pickImage.launch("image/*")
                    if (image != null) {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            if (isAdded && context != null) {
                                val response = UserModule.updateUser(
                                    token,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    image
                                )
                                if (response != "ok") {
                                    Toast.makeText(requireContext(), response, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                    }
                    true
                }
                R.id.editAvatar -> {
                    pickImage.launch("image/*")
                    if (image != null) {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            if (isAdded && context != null) {
                                val response = UserModule.updateUser(
                                    token,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    image,
                                    null
                                )

                                if (response != "ok") {
                                    Toast.makeText(requireContext(), response, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                    }
                    true
                }
                R.id.logout -> {
                    token = ""
                    user = User(UserData("", false, "", "", "", "", "", "", ""), emptyList(), emptyList())
                    TokenManager.clearToken(requireContext())
                    val context: Context? = activity
                    val intent = Intent(context, MainActivity::class.java)
                    context?.startActivity(intent)
                    activity?.finish()
                    true
                }
                // Add more cases for other menu items if needed
                else -> false
            }
        }

        toolbarButton = listOf(
            view.findViewById(R.id.activeProjectsBtn),
            view.findViewById(R.id.portfolioBtn),
            view.findViewById(R.id.collaborationBtn),
            view.findViewById(R.id.favoritesBtn))

        val inactiveTextColor = ContextCompat.getColor(requireContext(), R.color.black)
        val activeTextColor = ContextCompat.getColor(requireContext(), R.color.purple)


        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.menu_button)
        toolbarButton[0].setTextColor(activeTextColor)
        toolbarButton[0].background = drawable

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
                    R.id.activeProjectsBtn -> {
                        addPortfolioBtn.visibility = GONE
                        activeProjectsAdapter = ActiveProjectsAdapter(
                            requireContext(),
                            activeProjects,
                            ProjectType.ActiveProject
                        )
                        activeProjectsAdapter.setOnItemClickListener(this@ProfileFragment)
                        projectsRecycleView.layoutManager = LinearLayoutManager(requireContext())
                        projectsRecycleView.adapter = activeProjectsAdapter
                        uploadActiveProjects()
                    }
                    R.id.portfolioBtn -> {
                        addPortfolioBtn.visibility = VISIBLE
                        val adapter = PortfolioViewAdapter(requireContext(), portfolios)
                        adapter.setOnItemClickListener(this)
                        projectsRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
                        projectsRecycleView.adapter = adapter
                        uploadPortfolioProjects()
                    }
                    R.id.collaborationBtn -> {
                        addPortfolioBtn.visibility = GONE
                        activeProjectsAdapter = ActiveProjectsAdapter(
                            requireContext(),
                            collaborationProjects,
                            ProjectType.CollaborationProject
                        )
                        activeProjectsAdapter.setOnItemClickListener(this@ProfileFragment)
                        projectsRecycleView.layoutManager = LinearLayoutManager(requireContext())
                        projectsRecycleView.adapter = activeProjectsAdapter
                        uploadCollaborationProjects()
                    }
                    R.id.favoritesBtn -> {
                        addPortfolioBtn.visibility = GONE
                        activeProjectsAdapter = ActiveProjectsAdapter(
                            requireContext(),
                            favoriteProjects,
                            ProjectType.FavoriteProject
                        )
                        activeProjectsAdapter.setOnItemClickListener(this@ProfileFragment)
                        projectsRecycleView.layoutManager = LinearLayoutManager(requireContext())
                        projectsRecycleView.adapter = activeProjectsAdapter
                        uploadFavoritesProjects()
                    }
                }
            }
        }


    }

    private fun uploadActiveProjects(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                activeProjects = OrderModule.getMyOrders(token, user.userData.id).toMutableList()
                if (isAdded && context != null) {
                    activeProjectsAdapter = ActiveProjectsAdapter(
                        requireContext(),
                        activeProjects,
                        ProjectType.ActiveProject
                    )
                    activeProjectsAdapter.setOnItemClickListener(this@ProfileFragment)
                    projectsRecycleView.layoutManager = LinearLayoutManager(requireContext())
                    projectsRecycleView.adapter = activeProjectsAdapter
                }
            }
        }
    }

    private fun uploadFavoritesProjects(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
            favoriteProjects = OrderModule.getMyFavoriteOrders(token, user.userData.id).toMutableList()
                activeProjectsAdapter = ActiveProjectsAdapter(
                    requireContext(),
                    favoriteProjects,
                    ProjectType.FavoriteProject
                )
                activeProjectsAdapter.setOnItemClickListener(this@ProfileFragment)
                projectsRecycleView.layoutManager = LinearLayoutManager(requireContext())
                projectsRecycleView.adapter = activeProjectsAdapter
            }
        }
    }

    private fun uploadCollaborationProjects(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
            collaborationProjects = OrderModule.getMyCollaborationOrders(token, user.userData.id).toMutableList()
                activeProjectsAdapter = ActiveProjectsAdapter(
                    requireContext(),
                    collaborationProjects,
                    ProjectType.CollaborationProject
                )
                activeProjectsAdapter.setOnItemClickListener(this@ProfileFragment)
                projectsRecycleView.layoutManager = LinearLayoutManager(requireContext())
                projectsRecycleView.adapter = activeProjectsAdapter
            }
        }
    }

    private fun uploadPortfolioProjects(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
            portfolios = PortfolioModule.getMyPortfolios(token, user.userData.id).toMutableList()

                val adapter = PortfolioViewAdapter(
                    requireContext(),
                    portfolios
                )
                adapter.setOnItemClickListener(this@ProfileFragment)
                projectsRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
                projectsRecycleView.adapter = adapter
            }
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
            ProjectType.FavoriteProject -> {
                if (position < favoriteProjects.size){
                    projectId = favoriteProjects[position].id
                }
                else{
                    return
                }
            }
            else -> return
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {

                val projectResponse = OrderModule.getOrder(token, projectId)
                if (projectResponse == null) {
                    Toast.makeText(requireContext(), "Error on server", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val context: Context? = activity

                val intent = Intent(context, ProjectActivity::class.java)

                intent.putExtra("project", projectResponse)

                context?.startActivity(intent)
            }
        }
    }

    override fun onItemClick(position: Int) {

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {

                val portfolioResponse = PortfolioModule.getPortfolio(token, portfolios[position].id)
                if (portfolioResponse == null) {
                    Toast.makeText(requireContext(), "Error on server", Toast.LENGTH_LONG).show()
                    return@launch
                }
                val context: Context? = activity

                val intent = Intent(context, PortfolioActivity::class.java)

                intent.putExtra("portfolio", portfolioResponse)

                context?.startActivity(intent)
            }
        }
    }
}