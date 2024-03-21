package com.example.collart.MainPage.Home.Projects

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.collart.Auth.CurrentUser
import com.example.collart.Auth.CurrentUser.token
import com.example.collart.NetworkSystem.UserModule
import com.example.collart.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProjectsViewAdapter(private val projects: List<Project>, private val context: Context) : RecyclerView.Adapter<ProjectsViewAdapter.ProjectViewHolder>() {


    interface OnItemClickListener {
        fun onProjectClick(position: Int)
        fun onJoinButtonClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val projectView: View = itemView.findViewById(R.id.projectView)
        val projectImageView: ImageView = projectView.findViewById(R.id.projectImageView)
        val projectNameView: TextView = projectView.findViewById(R.id.projectNameView)
        val projectSpecialistView: TextView = projectView.findViewById(R.id.projectSpecialistView)
        val projectDescriptionView: TextView = projectView.findViewById(R.id.shortDescriptionView)
        val projectExperienceView: TextView = projectView.findViewById(R.id.experienceProjectView)
        val projectProgramsView: TextView = projectView.findViewById(R.id.programsProjectView)
        val authorImageView: ImageView = projectView.findViewById(R.id.authorImageView)
        val authorNameView: TextView = projectView.findViewById(R.id.authorNameView)
        val joinProjectBtn: Button = itemView.findViewById(R.id.joinProjectButton)

    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener?.onProjectClick(position)
        }

        val project = projects[position]

        val urlImage: String = project.image.replace("http://", "https://")
        val urlAvatar = project.authorImage.replace("http://", "https://")
        Glide.with(context)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(holder.authorImageView)
        Glide.with(context)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(holder.projectImageView)

        holder.projectNameView.text = project.name
        holder.projectSpecialistView.text = project.profession
        holder.projectDescriptionView.text = "Что требуется: " + project.description
        holder.projectExperienceView.text = "Опыт работы: " + project.experience.stringValue

        var programs = "Программы: "
        for (program in project.programs){
            programs += program + ", "
        }
        programs = programs.dropLast(2)

        holder.projectProgramsView.text = programs
        holder.authorNameView.text = project.authorName

        holder.joinProjectBtn.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                if (CurrentUser.user == null){
                    CurrentUser.user = UserModule.getCurrentUser(CurrentUser.token)
                }
                if (CurrentUser.user == null){
                    Toast.makeText(context, "Server error", Toast.LENGTH_SHORT).show()
                }
                listener?.onJoinButtonClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return projects.size
    }
}