package com.example.collart.Chat.AllChats

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.Auth.CurrentUser
import com.example.collart.Chat.Chat
import com.example.collart.Chat.ChatActivity
import com.example.collart.MainPage.Home.Projects.ProjectActivity
import com.example.collart.NetworkSystem.ChatModule
import com.example.collart.NetworkSystem.OrderModule
import com.example.collart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer
import java.util.concurrent.Executors


class ChatsFragment : Fragment(), ChatsViewAdapter.OnChatClickListener{

    private lateinit var recyclerView: RecyclerView

    private lateinit var chatsAdapter: ChatsViewAdapter

    private var chats: MutableList<Chat> = emptyList<Chat>().toMutableList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recycleChatsView)
        recyclerView.layoutManager = LinearLayoutManager(context)


        startChatListUpdater()
        chatsAdapter = ChatsViewAdapter(chats, requireContext())
        chatsAdapter.setOnItemClickListener(this)
        recyclerView.adapter = chatsAdapter
    }

    private fun startChatListUpdater() {
        val handler = Handler(Looper.getMainLooper())
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {

            fixedRateTimer("chatListUpdater", false, 0L, 2000L) {
                handler.post {
                    loadChats()
                }
            }
        }
    }
    private fun loadChats(){
        val token = CurrentUser.token
        GlobalScope.launch(Dispatchers.Main) {
            if (isAdded && context != null) {
                val updatedChatList = ChatModule.getMyChats(token, CurrentUser.user.userData.id)
                chats.clear()
                chats.addAll(updatedChatList)
                chatsAdapter.notifyDataSetChanged()
            }
        }
    }
    override fun onChatClick(position: Int) {

        val context: Context? = activity

        val intent = Intent(context, ChatActivity::class.java)

        intent.putExtra("Chat", chats[position])

        context?.startActivity(intent)
    }

}