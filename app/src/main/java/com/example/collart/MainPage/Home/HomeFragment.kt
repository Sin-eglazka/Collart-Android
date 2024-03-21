package com.example.collart.MainPage.Home

import ActiveProjectsAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.Auth.CurrentUser
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.MainPage.Home.Projects.ProjectsViewAdapter
import com.example.collart.MainPage.Home.Projects.SpecialistsViewAdapter
import com.example.collart.MainPage.Home.Specialists.Specialist
import com.example.collart.NetworkSystem.InteractionModule
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.Projects.ProjectActivity
import com.example.collart.R
import com.example.collart.UserPage.UserPageActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale


class HomeFragment : Fragment(), ProjectsViewAdapter.OnItemClickListener, SpecialistsViewAdapter.OnItemClickListener {


    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var homeNavMenuView: BottomNavigationView

    private lateinit var projects: MutableList<Project>
    private lateinit var specialists: MutableList<Specialist>
    private var myProjects: MutableList<Project> = emptyList<Project>().toMutableList()


    private var searchProjects: MutableList<Project> = mutableListOf()
    private var searchSpecialists: MutableList<Specialist> = mutableListOf()

    private lateinit var projectAdapter: ProjectsViewAdapter
    private lateinit var specAdapter: SpecialistsViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.recycleHomeView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        projectAdapter = ProjectsViewAdapter(searchProjects, requireContext())
        projectAdapter.setOnItemClickListener(this)
        recyclerView.adapter = projectAdapter


        //first initialization
        projects = mutableListOf()
        getProjects()
        searchProjects = projects.toMutableList()

        specialists = mutableListOf()
        getSpecialists()
        searchSpecialists = specialists.toMutableList()

        homeNavMenuView = view.findViewById(R.id.homeNavMenu)

        homeNavMenuView.setOnItemSelectedListener {
            when(it.itemId){

                R.id.projects -> {
                    searchProjects.clear()
                    searchProjects.addAll(projects)
                    projectAdapter = ProjectsViewAdapter(searchProjects, requireContext())
                    projectAdapter.setOnItemClickListener(this)
                    recyclerView.adapter = projectAdapter


                }

                R.id.specialists -> {
                    searchSpecialists.clear()
                    searchSpecialists.addAll(specialists)
                    specAdapter = SpecialistsViewAdapter(searchSpecialists, requireContext())
                    specAdapter.setOnItemClickListener(this)
                    recyclerView.adapter = specAdapter

                }

            }
            true
        }

        val projectBar = homeNavMenuView.menu.findItem(R.id.projects)
        val specialistBar = homeNavMenuView.menu.findItem(R.id.specialists)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    if (projectBar.isChecked){
                        searchProjects.clear()
                        projects.forEach{
                            if (it.name.toLowerCase(Locale.getDefault()).contains(searchText)){
                                searchProjects.add(it)
                            }
                        }
                    }
                    if (specialistBar.isChecked){
                        searchSpecialists.clear()
                        specialists.forEach{
                            if (it.name.toLowerCase(Locale.getDefault()).contains(searchText)){
                                searchSpecialists.add(it)
                            }
                        }
                    }
                }
                else{
                    if (projectBar.isChecked){
                        searchProjects.addAll(projects)
                    }
                    if (specialistBar.isChecked){
                        searchSpecialists.addAll(specialists)
                    }
                }
                recyclerView.adapter!!.notifyDataSetChanged()
                return false
            }
        })
    }

    override fun onProjectClick(position: Int) {
        GlobalScope.launch(Dispatchers.Main) {

            val projectResponse = OrderModule.getOrder(CurrentUser.token ,projects[position].id)
            if (projectResponse == null){
                Toast.makeText(requireContext(), "Error on server", Toast.LENGTH_LONG).show()
                return@launch
            }
            val context: Context? = activity

            val intent = Intent(context, ProjectActivity::class.java)

            intent.putExtra("project", projectResponse)

            context?.startActivity(intent)
        }
    }

    override fun onSpecialistClick(position: Int) {
        GlobalScope.launch(Dispatchers.Main) {

            val userResponse = UserModule.getSpecialist(CurrentUser.token ,specialists[position].id)
            if (userResponse == null){
                Toast.makeText(requireContext(), "Error on server", Toast.LENGTH_LONG).show()
                return@launch
            }
            val context: Context? = activity

            val intent = Intent(context, UserPageActivity::class.java)

            intent.putExtra("Specialist", userResponse)

            context?.startActivity(intent)
        }
    }

    override fun onInviteClick(position: Int) {
        val dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(R.layout.choose_project_sheet, null)

        val recycleMyProjects = view.findViewById<RecyclerView>(R.id.myProjectsView)
        recycleMyProjects.layoutManager = LinearLayoutManager(context)
        var adapter = ActiveProjectsAdapter(requireContext(), myProjects, ProjectType.ChooseActiveProject)
        recycleMyProjects.adapter = adapter

        GlobalScope.launch(Dispatchers.Main) {
            myProjects = OrderModule.getMyOrders(CurrentUser.token).toMutableList()
            adapter = ActiveProjectsAdapter(requireContext(), myProjects, ProjectType.ChooseActiveProject)
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
                            specialists[position].id,
                            myProjects[index].id
                        )
                        if (response == "ok"){
                            Toast.makeText(requireContext(), "Invite send", Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(), "Choose one project", Toast.LENGTH_LONG).show()
            }
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    override fun onJoinButtonClick(position: Int) {

        val userId: String = CurrentUser.user?.userData?.id.toString()
        GlobalScope.launch(Dispatchers.Main) {
            val response = InteractionModule.createInteraction(
                CurrentUser.token,
                userId,
                projects[position].authorId,
                projects[position].id
            )
            if (response == "ok"){
                Toast.makeText(requireContext(), "Request send", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
            }
        }
    }

    // TODO: Add filters
    private fun getProjects() {
        val token = CurrentUser.token
        GlobalScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                val orders = OrderModule.getAllOrders(token)
                if (orders != null) {
                    projects = orders.toMutableList()
                    searchProjects = projects.toMutableList()
                    projectAdapter = ProjectsViewAdapter(searchProjects, requireContext())
                    projectAdapter.setOnItemClickListener(this@HomeFragment)
                    recyclerView.adapter = projectAdapter
                }
            }
        }
    }

    // TODO: Add filters
    private fun getSpecialists(){
        val token = CurrentUser.token
        GlobalScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                val specialistsResponse = UserModule.getAllSpecialist(token)
                if (specialistsResponse != null) {
                    specialists = specialistsResponse.toMutableList()
                    searchSpecialists = specialists.toMutableList()
                }
            }
        }
    }


}