import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.R

enum class ProjectType{
    ActiveProject,
    ChooseActiveProject,
    CollaborationProject,
    FavoriteProject
}

class ActiveProjectsAdapter(private val context: Context, private val items: List<Project>, private var type: ProjectType) :
    RecyclerView.Adapter<ActiveProjectsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, type: ProjectType)
    }

    private var listener: OnItemClickListener? = null

    var selectedItemPosition: Int = RecyclerView.NO_POSITION


    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageProjectView: ImageView = itemView.findViewById(R.id.imageProjectView)
        val titleProjectView: TextView = itemView.findViewById(R.id.titleProjectView)
        val checkedProjectView: ImageView = itemView.findViewById(R.id.checkedProjectView)

        init {
            if (type == ProjectType.ChooseActiveProject) {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        selectedItemPosition = position
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.my_project_select, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (type != ProjectType.ChooseActiveProject){
            holder.itemView.setOnClickListener {
                listener?.onItemClick(position, type)
            }
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


        if (type == ProjectType.ChooseActiveProject) {
            if (position == selectedItemPosition) {
                holder.checkedProjectView.setImageResource(R.drawable.check)
            } else {
                holder.checkedProjectView.setImageDrawable(null)
            }
            holder.itemView.isSelected = position == selectedItemPosition
        }

    }


    override fun getItemCount(): Int {
        return items.size
    }
}