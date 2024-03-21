package com.example.collart.MainPage.Home.Projects

import android.content.Context
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.MainPage.Home.Specialists.Specialist
import com.example.collart.R

class SpecialistsViewAdapter(private val specialists: List<Specialist>, private val context: Context) : RecyclerView.Adapter<SpecialistsViewAdapter.SpecialistViewHolder>() {


    interface OnItemClickListener {
        fun onSpecialistClick(position: Int)
        fun onInviteClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    inner class SpecialistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val specialistBackgroundImageView: ImageView = itemView.findViewById(R.id.backgroundSpecialistView)
        val specialistNameView: TextView = itemView.findViewById(R.id.nameSpecialistView)
        val specialistProfessionView: TextView = itemView.findViewById(R.id.professionSpecialistView)
        val specialistExperienceView: TextView = itemView.findViewById(R.id.experienceSpecialsitView)
        val specialistProgramsView: TextView = itemView.findViewById(R.id.programsSpecialistView)
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val inviteToProjectBtn: Button = itemView.findViewById(R.id.inviteSpecialistButton)

    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.specialist_item, parent, false)
        return SpecialistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SpecialistViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener?.onSpecialistClick(position)
        }

        val specialist = specialists[position]

        val urlImage: String = specialist.backGroundImage.replace("http://", "https://")
        val urlAvatar = specialist.avatarImage.replace("http://", "https://")
        Glide.with(context)
            .load(urlAvatar)
            .placeholder(R.drawable.loading)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(holder.avatarImageView)
        Glide.with(context)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(holder.specialistBackgroundImageView)

        holder.specialistNameView.text = specialist.name

        var professions = ""
        for (profession in specialist.profession){
            professions += profession + "\n"
        }
        holder.specialistProfessionView.text = professions
        holder.specialistExperienceView.text = "Опыт работы: " + specialist.experience.stringValue

        var programs = "Программы: "
        for (program in specialist.programs){
            programs += program + ", "
        }
        programs = programs.dropLast(2)

        holder.specialistProgramsView.text = programs

        holder.inviteToProjectBtn.setOnClickListener {
            listener?.onInviteClick(position)
        }

    }

    override fun getItemCount(): Int {
        return specialists.size
    }
}