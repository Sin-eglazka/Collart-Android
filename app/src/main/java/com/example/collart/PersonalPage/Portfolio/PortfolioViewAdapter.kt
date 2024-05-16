package com.example.collart.PersonalPage.Portfolio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.NetworkSystem.Portfolio
import com.example.collart.R

class PortfolioViewAdapter(private val context: Context, private val items: List<Portfolio>) :
    RecyclerView.Adapter<PortfolioViewAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageProjectView: ImageView = itemView.findViewById(R.id.imageProjectView)
        val titleProjectView: TextView = itemView.findViewById(R.id.titleProjectView)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.short_portfolio, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
                listener?.onItemClick(position)
        }

        val project = items[position]

        val urlImage: String = project.image.replace("http://", "https://")

        Glide.with(context)
            .load(urlImage)
            .placeholder(R.drawable.loading)
            .error(R.drawable.black_picture)
            .centerCrop()
            .into(holder.imageProjectView)

        holder.titleProjectView.text = project.name

    }


    override fun getItemCount(): Int {
        return items.size
    }
}