package com.example.collart.Chat

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.R
import com.example.collart.Tools.TimeConverter.TimeConverter


class MyMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
    val messageText: TextView = view.findViewById(R.id.text_gchat_message_me)
    val date: TextView = view.findViewById(R.id.text_gchat_date_me)
    val messageDate: TextView = view.findViewById(R.id.text_gchat_timestamp_me)
    fun bindView(context: Context, message: Message, needDay: Boolean) {
        messageText.setText(message.message)
        messageDate.text = TimeConverter.GetTimeOfDate(message.createTime)
        if (needDay) {
            date.visibility = View.VISIBLE
            date.text = TimeConverter.GetDayOfDate(message.createTime)
        }
        else{
            date.visibility = View.GONE
        }
    }
}