package com.example.collart.Chat.AllChats

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.Chat.Chat
import com.example.collart.R
import com.example.collart.Tools.TimeConverter.TimeConverter

class ChatsViewAdapter(private val chats: List<Chat>, private val context: Context) : RecyclerView.Adapter<ChatsViewAdapter.ChatViewHolder>() {

    interface OnChatClickListener {
        fun onChatClick(position: Int)
    }

    private var listener: OnChatClickListener? = null

    private val readCheck = ContextCompat.getDrawable(context, R.drawable.read_check)
    private val unReadCheck = ContextCompat.getDrawable(context, R.drawable.unread_check)

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImageView: ImageView = itemView.findViewById(R.id.userImageView)
        val authorNameView: TextView = itemView.findViewById(R.id.authorNameView)
        val timeView: TextView = itemView.findViewById(R.id.timeView)
        val lastMessageView: TextView = itemView.findViewById(R.id.lastMessageView)
        val checkedProjectView: ImageView = itemView.findViewById(R.id.checkedProjectView)
        val numberNotificationText: TextView = itemView.findViewById(R.id.numberNotificationText)
        val notificationView: View = itemView.findViewById(R.id.notificationChatView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: OnChatClickListener?) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener?.onChatClick(position)
        }

        val chat = chats[position]

        val urlAvatar: String = chat.userPhoto.replace("http://", "https://")
        Glide.with(context)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(holder.userImageView)

        holder.authorNameView.text = chat.userName
        holder.lastMessageView.text = chat.lastMessage

        if (chat.unreadMessages != 0){
            holder.checkedProjectView.visibility = GONE
            holder.notificationView.visibility = VISIBLE
            holder.numberNotificationText.text = chat.unreadMessages.toString()
        }
        else{
            holder.checkedProjectView.visibility = VISIBLE
            holder.notificationView.visibility = GONE
            if (chat.isRead){
                holder.checkedProjectView.setImageDrawable(readCheck)
            }
            else{
                holder.checkedProjectView.setImageDrawable(unReadCheck)
            }
        }

        holder.timeView.text = TimeConverter.GetTimeOfChat(chat.messageTime)

    }

    override fun getItemCount(): Int {
        return chats.size
    }
}