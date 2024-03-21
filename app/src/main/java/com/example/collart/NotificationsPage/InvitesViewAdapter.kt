package com.example.collart.MainPage.Home.Projects

import android.content.Context
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.Auth.CurrentUser
import com.example.collart.MainPage.Home.HomeFragment
import com.example.collart.NetworkSystem.InteractionResponse
import com.example.collart.NotificationsPage.NotificationFragment
import com.example.collart.NotificationsPage.OnInteractionClickListener
import com.example.collart.R

class InvitesViewAdapter(private val invites: List<InteractionResponse>, private val context: Context) : RecyclerView.Adapter<InvitesViewAdapter.InviteViewHolder>() {

    private var listener: OnInteractionClickListener? = null

    inner class InviteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectView: View = itemView.findViewById(R.id.inviteProjectView)
        val projectImageView: ImageView = projectView.findViewById(R.id.projectImageView)
        val projectNameView: TextView = projectView.findViewById(R.id.projectNameView)
        val projectSpecialistView: TextView = projectView.findViewById(R.id.projectSpecialistView)
        val projectDescriptionView: TextView = projectView.findViewById(R.id.shortDescriptionView)
        val projectExperienceView: TextView = projectView.findViewById(R.id.experienceProjectView)
        val projectProgramsView: TextView = projectView.findViewById(R.id.programsProjectView)
        val authorImageView: ImageView = projectView.findViewById(R.id.authorImageView)
        val authorNameView: TextView = projectView.findViewById(R.id.authorNameView)
        val rejectButton: Button = itemView.findViewById(R.id.rejectInviteBtn)
        val joinButton: Button = itemView.findViewById(R.id.acceptInviteBtn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.invite_item, parent, false)
        return InviteViewHolder(itemView)
    }

    fun setOnItemClickListener(listener: OnInteractionClickListener?) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        val interaction = invites[position]

        val urlImage: String = interaction.order.order.image.replace("http://", "https://")
        Glide.with(context)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(holder.projectImageView)

        val urlAvatar: String = interaction.sender.userData.userPhoto.replace("http://", "https://")
        Glide.with(context)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(holder.authorImageView)

        holder.projectNameView.text = interaction.order.order.title
        holder.projectSpecialistView.text = interaction.order.skill?.nameRu
        holder.projectDescriptionView.text = "Что требуется: " + interaction.order.order.taskDescription
        holder.projectExperienceView.text = "Опыт работы: " + Experience.fromString(interaction.order.order.experience).stringValue

        var programs = "Программы: "
        for (program in interaction.order.tools){
            programs += program + ", "
        }
        programs = programs.dropLast(2)

        holder.projectProgramsView.text = programs
        holder.authorNameView.text = interaction.sender.userData.name + " " + interaction.sender.userData.surname

        if (interaction.status == "active") {
            holder.joinButton.setOnClickListener {
                listener?.onAcceptClick(interaction.id, interaction.getter.userData.id)
            }

            holder.rejectButton.setOnClickListener {
                listener?.onRejectClick(interaction.id, interaction.getter.userData.id)
            }
        }
        else{
            holder.joinButton.visibility = View.GONE
            holder.rejectButton.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return invites.size
    }
}