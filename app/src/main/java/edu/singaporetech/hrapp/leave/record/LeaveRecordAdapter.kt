package edu.singaporetech.hrapp.leave.record


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.leave.main.LeaveUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Class that connects the Leave record Recyclerview to the LeaveRecordFragment
 * @return RecyclerView.Adapter<LeaveRecordAdapter.ViewHolder>
 */
class LeaveRecordAdapter() : RecyclerView.Adapter<LeaveRecordAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener
    private var leaveList = mutableListOf<LeaveRecord>()

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    /**
     * Function that triggers when an item in the recycler view is clicked
     * @param listener
     */
    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    /**
     * Function that gets the list size
     * @return leaveList.size
     */
    override fun getItemCount(): Int {
        return leaveList.size
    }

    /**
     * Function that set data from the leave list
     * @param leaves
     */
    fun setData(leaves : ArrayList<LeaveRecord>) {
        this.notifyDataSetChanged()
        this.leaveList = leaves
    }

    /**
     * Function that retrieves approved data into the leavelist
     * @return leaveList
     */
    fun getData(): MutableList<LeaveRecord> {
        return leaveList
    }

    /**
     * Function that changes the colours of the progress bars
     * @param type
     */
    private fun setColorBar(type : String) : Int {
        return if (type == "annual") {
            R.color.annual
        } else if (type == "sick") {
            R.color.sick
        } else if (type == "compassion"){
            R.color.compassion
        } else {
            R.color.bootstrap_gray_light
        }
    }

    /**
     * Function that creates the view holder for the recyclerview
     * @param parent
     * @param viewType
     * @return ViewHolder(v, mListener)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return ViewHolder(v, mListener)

    }

    /**
     * Function that Displays the recyclerview set with the properties defined
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = leaveList[position]

        holder.type.text = currentItem.type + " Leave"
        holder.dateApplied.text = "Date Applied: " + LeaveUtils.datesApplied(currentItem.from, currentItem.to)
        holder.status.text = currentItem.status

        if (holder.status.text == "Approved")
            holder.status.setBackgroundResource(R.color.green_approved)
        else if (holder.status.text == "Declined")
            holder.status.setBackgroundResource(R.color.red_rejected)
        else if (holder.status.text == "Declined")
            holder.status.setBackgroundResource(R.color.orange_pending)

        var days = LeaveUtils.getDays(currentItem.from, currentItem.to, currentItem.daytype)
        if (days % 1 == 0.0)
            holder.dayApplied.text = String.format("%.0f Day", days)
        else
            holder.dayApplied.text = days.toString() + " Days"
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val type: TextView
        var dateApplied: TextView
        var dayApplied: TextView
        var status: TextView
        var colorBar : View

        init {
            type = itemView.findViewById(R.id.leave_type)
            dateApplied = itemView.findViewById(R.id.applied_date)
            dayApplied = itemView.findViewById(R.id.applied_day)
            status = itemView.findViewById(R.id.leave_status)
            colorBar = itemView.findViewById(R.id.colored_bar)
            itemView.setOnClickListener() {
               listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}