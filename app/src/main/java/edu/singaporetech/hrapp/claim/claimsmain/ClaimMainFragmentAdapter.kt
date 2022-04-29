package edu.singaporetech.hrapp.claim.claimsmain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.claimrecords.Records
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Class for the Recyclerview of the ClaimMainFragmentAdapter
 * @return RecyclerView.Adapter<ClaimMainFragmentAdapter.ViewHolder>()
 */
class ClaimMainFragmentAdapter() : RecyclerView.Adapter<ClaimMainFragmentAdapter.ViewHolder>() {

    private var claimList = mutableListOf<Records>()
    private var claimData = mutableListOf<ClaimsData>()

    private val limit = 3

    /**
     * Creates the view holder for the recyclerview
     * @param parent
     * @param viewType
     * @return ClaimMainFragmentAdapter.ViewHolder
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClaimMainFragmentAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.claimsoverview_item, parent, false)
        return ViewHolder(v)

    }

    /**
     * Sets the view holder with the parameters according to the status of the claims
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = claimList[position]

        holder.type.text = currentItem.category + " Claim"
        var newdatetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(currentItem.dop.toString().toLong()), ZoneOffset.UTC)
        var formatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
        var formateddatetime = newdatetime.format(formatter)
        holder.dateApplied.text = "Date Submitted: " + formateddatetime
        holder.amount.text= "$" + currentItem.amount.toString()
        if (currentItem.status == "Approved"){
            holder.status.setBackgroundResource(R.color.green_approved)
        } else if (currentItem.status == "Pending"){
            holder.status.setBackgroundResource(R.color.orange_pending)
        } else if (currentItem.status == "Rejected"){
            holder.status.setBackgroundResource(R.color.red_rejected)
        } else if (currentItem.status == "Paid"){
            holder.status.setBackgroundResource(R.color.blue_paid)
        }
        holder.status.text = currentItem.status
    }

    /**
     * Gets the list size of the recyclerview
     * @return Int
     */
    override fun getItemCount(): Int {
        if(claimList.size > limit){
            return limit;
        }
        else{
            return claimList.size;
            }
        }

    /**
     * Sets the data of the recycler view onto the claimslist
     * @param claims
     */
    fun setData(claims: ArrayList<Records>) {
            this.claimList = claims
            notifyDataSetChanged()
        }

    /**
     * Sets the data of the remaining claims amount
     * @param data
     */
    fun setClaimsData(data: ArrayList<ClaimsData>) {
            this.claimData = data
        }

    /**
     * Function that returns the Claim data
     * @return MutableList<ClaimsData>
     */
    fun getClaimsData(): MutableList<ClaimsData> {
            return claimData
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val type: TextView
            var dateApplied: TextView
            var amount: TextView
            var status: TextView

            init {
                type = itemView.findViewById(R.id.claimType)
                dateApplied = itemView.findViewById(R.id.claimDate)
                amount = itemView.findViewById(R.id.claimAmount)
                status = itemView.findViewById(R.id.claimStatus)
            }
        }
    }




