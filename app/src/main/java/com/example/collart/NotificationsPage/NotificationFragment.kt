package com.example.collart.NotificationsPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.Auth.CurrentUser
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.MainPage.Home.Projects.InvitesViewAdapter
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.MainPage.Home.Projects.ResponsesViewAdapter
import com.example.collart.NetworkSystem.InteractionModule
import com.example.collart.NetworkSystem.InteractionModule.getResponsesOnMyProjects
import com.example.collart.NetworkSystem.InteractionResponse
import com.example.collart.NetworkSystem.Tool
import com.example.collart.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface OnInteractionClickListener {
    fun onAcceptClick(interactionId: String, getterId: String)
    fun onRejectClick(interactionId: String, getterId: String)
}

class NotificationFragment : Fragment(), OnInteractionClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationMenu: BottomNavigationView
    private lateinit var spinner: Spinner

    private var responses: MutableList<InteractionResponse> = emptyList<InteractionResponse>().toMutableList()
    private var invites: MutableList<InteractionResponse> = emptyList<InteractionResponse>().toMutableList()

    private var activeResponses: MutableList<InteractionResponse> = emptyList<InteractionResponse>().toMutableList()
    private var activeInvites: MutableList<InteractionResponse> = emptyList<InteractionResponse>().toMutableList()


    private lateinit var responseAdapter: ResponsesViewAdapter
    private lateinit var invitesAdapter: InvitesViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recycleNotificationView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        notificationMenu = view.findViewById(R.id.notificationNavMenu)

        //first initialization
        loadInvitesOnOtherProjects()
        invitesAdapter = InvitesViewAdapter(activeInvites, requireContext())
        invitesAdapter.setOnItemClickListener(this)

        loadResponsesOnMyProjects()
        responseAdapter = ResponsesViewAdapter(activeResponses, requireContext())
        recyclerView.adapter = responseAdapter


        notificationMenu.setOnItemSelectedListener {
            when(it.itemId){

                R.id.responses -> {
                    loadResponsesOnMyProjects()
                    responseAdapter = ResponsesViewAdapter(activeResponses, requireContext())
                    responseAdapter.setOnItemClickListener(this)
                    recyclerView.adapter = responseAdapter

                }

                R.id.invites -> {
                    loadInvitesOnOtherProjects()
                    invitesAdapter = InvitesViewAdapter(activeInvites, requireContext())
                    invitesAdapter.setOnItemClickListener(this)
                    recyclerView.adapter = invitesAdapter

                }

            }
            true
        }

        spinner = view.findViewById(R.id.spinnerFilter)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("Активные", "Принятые", "Отклоненные"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterByStatus(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun filterByStatus(position: Int){
        when(position){
            0 -> {
                activeInvites = invites.filter { it.status == "active" }.toMutableList()
                activeResponses = responses.filter { it.status == "active" }.toMutableList()
            }
            1 -> {
                activeInvites = invites.filter { it.status == "accepted" }.toMutableList()
                activeResponses = responses.filter { it.status == "accepted" }.toMutableList()
            }
            2 -> {
                activeInvites = invites.filter { it.status == "rejected" }.toMutableList()
                activeResponses = responses.filter { it.status == "rejected" }.toMutableList()
            }
        }
        if (notificationMenu.selectedItemId == R.id.responses){
            responseAdapter = ResponsesViewAdapter(activeResponses, requireContext())
            responseAdapter.setOnItemClickListener(this@NotificationFragment)
            recyclerView.adapter = responseAdapter
        }
        else{
            invitesAdapter = InvitesViewAdapter(activeInvites, requireContext())
            invitesAdapter.setOnItemClickListener(this@NotificationFragment)
            recyclerView.adapter = invitesAdapter
        }
    }

    private fun loadResponsesOnMyProjects(){
        val token = CurrentUser.token
        GlobalScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                responses = InteractionModule.getResponsesOnMyProjects(token).toMutableList()
                filterByStatus(spinner.selectedItemPosition)
            }
        }
    }

    private fun loadInvitesOnOtherProjects(){
        val token = CurrentUser.token
        GlobalScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                invites = InteractionModule.getInvitesOnOtherProjects(token).toMutableList()
                filterByStatus(spinner.selectedItemPosition)
            }
        }
    }

    override fun onAcceptClick(interactionId: String, getterId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val token = CurrentUser.token
            val response = InteractionModule.acceptInteraction(token, interactionId, getterId)
            if (response){
                Toast.makeText(context, "Invite accepted", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRejectClick(interactionId: String, getterId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val token = CurrentUser.token
            val response = InteractionModule.rejectInteraction(token, interactionId, getterId)
            if (response){
                Toast.makeText(context, "Invite rejected", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }
}