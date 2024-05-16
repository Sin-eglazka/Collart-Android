package com.example.collart.MainPage.Home.Projects

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.NetworkSystem.InteractionResponse
import com.example.collart.NotificationsPage.OnInteractionClickListener
import com.example.collart.R

class ResponsesViewAdapter(private val responses: List<InteractionResponse>, private val context: Context) : RecyclerView.Adapter<ResponsesViewAdapter.ResponseViewHolder>() {

    private var listener: OnInteractionClickListener? = null

    inner class ResponseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectImageView: ImageView = itemView.findViewById(R.id.projectImageView)
        val inviteTextView: TextView = itemView.findViewById(R.id.authorInviteView)
        val authorImageView: ImageView = itemView.findViewById(R.id.authorInviteImageView)
        val rejectButton: Button = itemView.findViewById(R.id.rejectResponseBtn)
        val joinButton: Button = itemView.findViewById(R.id.acceptResponseBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.response_item, parent, false)
        return ResponseViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: OnInteractionClickListener?) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val response = responses[position]

        val urlImage: String = response.order.order.image.replace("http://", "https://")
        Glide.with(context)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(holder.projectImageView)

        val urlAvatar: String = response.sender.userData.userPhoto.replace("http://", "https://")
        Glide.with(context)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(holder.authorImageView)

        holder.inviteTextView.text =
            "${response.sender.userData.name + " " + response.sender.userData.surname} хочет присоединиться к Вашему проекту \"${response.order.order.title}\""

        if (response.status == "active") {
            holder.joinButton.setOnClickListener {
                listener?.onAcceptClick(response.id, response.getter.userData.id)
            }
            holder.rejectButton.setOnClickListener {
                listener?.onRejectClick(response.id, response.getter.userData.id)
            }
        }
        else{
            holder.joinButton.visibility = View.GONE
            holder.rejectButton.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return responses.size
    }
}