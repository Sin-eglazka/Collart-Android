package com.example.collart.Chat

import MessagesAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.setContentView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.Auth.CurrentUser
import com.example.collart.Auth.User
import com.example.collart.NetworkSystem.ChatModule
import com.example.collart.NetworkSystem.ChatModule.sendMessage
import com.example.collart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity(), MessagesAdapter.ReadListener {

    private lateinit var chat: Chat

    private lateinit var adapter: MessagesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendMessageBtn: Button
    private lateinit var editMessage: EditText

    private var lastMessageTime: String = ""
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat = intent.getSerializableExtra("Chat") as Chat

        val toolbar: Toolbar = findViewById(R.id.toolbarChat)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(chat.userName)

        toolbar.setNavigationOnClickListener {
            onBackPressed() // Handle back button click
        }

        recyclerView = findViewById(R.id.recycleChatView)
        sendMessageBtn = findViewById(R.id.sendBtn)
        editMessage = findViewById(R.id.editMessageView)
        sendMessageBtn.setOnClickListener {
            sendMessage()
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = MessagesAdapter(this)
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition == 0) {
                    // Load older messages from the server
                    loadOlderMessages()
                }
            }
        })

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Check for new messages from the server
                checkForNewMessages()
                // Run this runnable again after 1 second
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun sendMessage()
    {
        val textMessage = editMessage.text.toString()
        if (!textMessage.isNullOrBlank()){
            GlobalScope.launch(Dispatchers.Main) {
                val message = ChatModule.sendMessage(
                    CurrentUser.token,
                    textMessage,
                    CurrentUser.user.userData.id,
                    chat.userId,
                    emptyList()
                )
                if (message != null){
                    //adapter.addFirst(message)
                    editMessage.setText("")
                    //lastMessageTime = message.createTime
                }

            }
        }
    }

    private fun loadOlderMessages(){
        GlobalScope.launch(Dispatchers.Main) {
            val messages = ChatModule.getChatMessages(CurrentUser.token, CurrentUser.user.userData.id, chat.userId, offset, 15)
            adapter.loadOlderMessages(messages.toMutableList())
            offset += messages.size

        }
    }

    private fun checkForNewMessages(){
        GlobalScope.launch(Dispatchers.Main) {
            val messages = ChatModule.getChatMessages(CurrentUser.token, CurrentUser.user.userData.id, chat.userId, 0, 15)
            val newMessages = messages.filter { it.createTime > lastMessageTime }
            if (newMessages.isNotEmpty()) {
                lastMessageTime = newMessages.last().createTime
                adapter.loadNewMessages(newMessages.toMutableList())
                offset += newMessages.size
            }
        }
    }

    override fun readMessages() {
        GlobalScope.launch(Dispatchers.Main) {
            ChatModule.readMessages(CurrentUser.token, CurrentUser.user.userData.id, chat.userId)

        }
    }
}