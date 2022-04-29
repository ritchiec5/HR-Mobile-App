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
 * Class that connects the recyclerview to the LeavesOverviewFragment
 * @return RecyclerView.Adapter<LeaveOverviewAdapter.ViewHolder>
 */
class LeaveOverviewAdapter() : RecyclerView.Adapter<LeaveOverviewAdapter.ViewHolder>() {

    private var leaveList = mutableListOf<LeaveRecord>()
    private var leaveData = mutableListOf<LeaveData>()

    private val limit = 3

    /**
     * Function that creates the view holder for the recyclerview
     * @param parent
     * @param viewType
     * @return ViewHolder(v)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveOverviewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.overview_item, parent, false)
        return ViewHolder(v)

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

        if (holder.status.text == "Declined")
            holder.status.setBackgroundResource(R.color.red_rejected)

        var days = LeaveUtils.getDays(currentItem.from, currentItem.to, currentItem.daytype)
        if (days % 1 == 0.0)
            holder.dayApplied.text = String.format("%.0f Day", days)
        else
            holder.dayApplied.text = days.toString() + " Days"
    }

    /**
     * Function that gets the list size
     * @return leaveList.size
     * @return limit recent 3 items iin the recyclerview
     */
    override fun getItemCount(): Int {
        if(leaveList.size > limit){
            return limit;
        }
        else
        {
            return leaveList.size;
        }
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
     * Function that sets leave amount from the leave data list
     * @param data
     */
    fun setLeaveData(data: ArrayList<LeaveData>) {
        this.leaveData = data
    }

    /**
     * Function that retrieves leaves amount data
     * @return leaveData
     */
    fun getLeaveData(): MutableList<LeaveData> {
        return leaveData
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val type: TextView
        var dateApplied: TextView
        var dayApplied: TextView
        var status: TextView

        init {
            type = itemView.findViewById(R.id.leaveType1)
            dateApplied = itemView.findViewById(R.id.leaveDate1)
            dayApplied = itemView.findViewById(R.id.leaveDay1)
            status = itemView.findViewById(R.id.leaveStatus1)
        }
    }
}