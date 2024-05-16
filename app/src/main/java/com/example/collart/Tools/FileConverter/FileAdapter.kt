// FileAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.R
import com.example.collart.Tools.FileConverter.FileConverter

class FileAdapter(private val files: List<String>) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    interface OnItemClickListener {
        fun onFileClick(url: String)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)

        init {
            itemView.setOnClickListener {
                listener?.onFileClick(files[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.file_placeholder, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = files[position]
        holder.fileNameTextView.text = FileConverter.extractFileNameFromUrl(currentItem)
    }

    override fun getItemCount() = files.size
}
