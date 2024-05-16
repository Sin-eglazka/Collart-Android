package com.example.collart.Chat

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.R
import com.example.collart.Tools.TimeConverter.TimeConverter

class OtherMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val messageText: TextView = view.findViewById(R.id.text_gchat_message_other)
    private val date: TextView = view.findViewById(R.id.text_gchat_date_other)
    private val timestamp: TextView = view.findViewById(R.id.text_gchat_timestamp_other)
    fun bindView(context: Context, message: Message, needDay: Boolean) {
        messageText.text = message.message
        timestamp.text = TimeConverter.GetTimeOfDate(message.createTime)
        if (needDay) {
            date.visibility = View.VISIBLE
            date.text = TimeConverter.GetDayOfDate(message.createTime)
        }
        else{
            date.visibility = View.GONE
        }
    }
}