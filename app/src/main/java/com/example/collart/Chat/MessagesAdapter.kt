import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.collart.Auth.CurrentUser
import com.example.collart.Chat.Message
import com.example.collart.Chat.MyMessageHolder
import com.example.collart.Chat.OtherMessageHolder
import com.example.collart.R
import com.example.collart.Tools.TimeConverter.TimeConverter

class MessagesAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_USER_MESSAGE_ME = 10
    private val VIEW_TYPE_USER_MESSAGE_OTHER = 11
    private var messages: MutableList<Message> = ArrayList()
    private var context: Context

    interface ReadListener {
        fun readMessages()
    }

    private var listener: ReadListener? = null
    init {
        this.context = context
    }

    fun setOnItemClickListener(listener: ReadListener?) {
        this.listener = listener
    }

    fun loadOlderMessages(messages: MutableList<Message>) {
        this.messages.addAll(0, messages)
        notifyDataSetChanged()
    }

    fun loadNewMessages(messages: MutableList<Message>){
        this.messages.addAll(messages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            VIEW_TYPE_USER_MESSAGE_ME  -> {
                MyMessageHolder(layoutInflater.inflate(R.layout.message_my_item, parent, false))
            }
            VIEW_TYPE_USER_MESSAGE_OTHER ->  {
                OtherMessageHolder(layoutInflater.inflate(R.layout.message_other_item, parent, false))
            }
            else -> MyMessageHolder(layoutInflater.inflate(R.layout.message_my_item, parent, false))
        }
    }
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.sender.id == CurrentUser.user.userData.id) {
            VIEW_TYPE_USER_MESSAGE_ME
        } else {
            VIEW_TYPE_USER_MESSAGE_OTHER
        }
    }
    override fun getItemCount() = messages.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var needDay = false
        if (position == 0){
            needDay = true
        }
        else{
            val currDay = TimeConverter.GetDayOfDate(messages[position].createTime)
            val prevDay = TimeConverter.GetDayOfDate(messages[position - 1].createTime)
            if (currDay != prevDay){
                needDay = true
            }
        }
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MyMessageHolder
                holder.bindView(context, messages[position], needDay)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherMessageHolder
                holder.bindView(context, messages[position], needDay)
                if (!messages[position].isRead){
                    listener?.readMessages()
                }
            }
        }
    }

}