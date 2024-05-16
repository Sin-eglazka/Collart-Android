package com.example.collart.Chat.AllChats

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.Auth.CurrentUser
import com.example.collart.Chat.Chat
import com.example.collart.Chat.ChatActivity
import com.example.collart.NetworkSystem.ChatModule
import com.example.collart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer


class ChatsFragment : Fragment(), ChatsViewAdapter.OnChatClickListener{

    private lateinit var recyclerView: RecyclerView

    private lateinit var chatsAdapter: ChatsViewAdapter

    private var chats: MutableList<Chat> = emptyList<Chat>().toMutableList()


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

        chatsAdapter = ChatsViewAdapter(chats, requireContext())
        chatsAdapter.setOnItemClickListener(this)
        recyclerView.adapter = chatsAdapter
        startChatListUpdater()
    }

    private fun startChatListUpdater() {
        val handler = Handler(Looper.getMainLooper())

        fixedRateTimer("chatListUpdater", false, 0L, 2000L) {
            handler.post {
                if (view != null) { // Check if fragment view is available
                    loadChats()
                }
            }
        }
    }
    private fun loadChats(){
        val token = CurrentUser.token
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val updatedChatList = ChatModule.getMyChats(token, CurrentUser.user.userData.id)
                chats.clear()
                chats.addAll(updatedChatList)
                chatsAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                // Handle exceptions if any
                e.printStackTrace()
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