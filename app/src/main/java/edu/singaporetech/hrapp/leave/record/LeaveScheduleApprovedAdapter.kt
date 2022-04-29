package edu.singaporetech.hrapp.leave.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.leave.main.LeaveData
import edu.singaporetech.hrapp.leave.main.LeaveUtils

/**
 * Class that connects the Approved Leaves Recyclerview to the LeaveScheduleFragment
 * @return RecyclerView.Adapter<LeaveScheduleApprovedAdapter.ViewHolder>
 */
class LeaveScheduleApprovedAdapter() : RecyclerView.Adapter<LeaveScheduleApprovedAdapter.ViewHolder>() {

    private var leaveList = mutableListOf<LeaveRecord>()
    private lateinit var mListener : onItemClickListener

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
     * Function that gets the list size
     * @return leaveList.size
     */
    override fun getItemCount(): Int {
        return leaveList.size;
    }

    /**
     * Function that creates the view holder for the recyclerview
     * @param parent
     * @param viewType
     * @return ViewHolder(v, mListener)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveScheduleApprovedAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.approved_schedule_item, parent, false)
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
        holder.dateApplied.text = LeaveUtils.datesApplied(currentItem.from, currentItem.to)

        var days = LeaveUtils.getDays(currentItem.from, currentItem.to, currentItem.daytype)
        if (days % 1 == 0.0)
            holder.dayApplied.text = String.format("%.0f Day(s)", days)
        else
            holder.dayApplied.text = days.toString() + " Days(s)"

        if (currentItem.status == "Pending" || currentItem.status == "Declined"){
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }

        else if (currentItem.status == "Approved"){
            holder.itemView.visibility = View.VISIBLE

            val LayoutParamsApproved = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            LayoutParamsApproved.setMargins(10, 10, 10, 10)
            holder.itemView.setLayoutParams(LayoutParamsApproved)

        }
    }


    inner class ViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val type: TextView
        var dateApplied: TextView
        var dayApplied: TextView

        init {
            itemView.setOnClickListener() {
                listener.onItemClick(bindingAdapterPosition)
            }
            type = itemView.findViewById(R.id.leaveType)
            dateApplied = itemView.findViewById(R.id.leaveDate)
            dayApplied = itemView.findViewById(R.id.leaveDuration)
        }
    }
}