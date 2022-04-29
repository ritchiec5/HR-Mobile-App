package edu.singaporetech.hrapp.claim.claimrecords

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.R.color.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Adapter Class for the recyclerview of the ClaimRecordFragment
 * @return RecyclerView.Adapter<Claim_RecordAdapter.ItemViewHolder>()
 */
class Claim_RecordAdapter () : RecyclerView.Adapter<Claim_RecordAdapter.ItemViewHolder>(){

    private lateinit var mListener : onItemClickListener
    private var claimList = mutableListOf<Records>()

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    /**
     * Onclick function for each item on the recyclerview
     * @param listener
     */
    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    /**
     * Sets data for the claim records
     * @param claims
     */
    fun setData(claims : ArrayList<Records>) {
        this.claimList = claims
        notifyDataSetChanged()
    }

    /**
     * Returns the claims list containing data of the records
     * @return MutableList<Records>
     */
    fun getData(): MutableList<Records> {
        return claimList
    }

    inner class ItemViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val dopTextView: TextView
        val categoryTextView: TextView
        val amountTextView: TextView
        val statusTextView: TextView

        init{
            dopTextView = itemView.findViewById(R.id.textViewDate)
            categoryTextView = itemView.findViewById(R.id.textViewCategory)
            amountTextView = itemView.findViewById(R.id.textViewAmount)
            statusTextView = itemView.findViewById(R.id.textViewStatus)

            itemView.setOnClickListener(){
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }

    /**
     * Function that creates the view holder
     * @param parent
     * @param viewType
     * @return ItemViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_claims_itemview, parent, false)
        return ItemViewHolder(view, mListener)
    }


    /**
     * Function that initializes the viewholder of
     * the recyclerview, with setting the color coded claim statuses
     * @param holder
     * @param pos
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ItemViewHolder, pos: Int) {
        val record : Records = claimList[pos]
        var newdatetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(record.dop), ZoneOffset.UTC)
        var formatter = DateTimeFormatter.ofPattern("EEE ,dd MMM yyyy")
        var formateddatetime = newdatetime.format(formatter)
        val datetime = formateddatetime.toString()
        holder.dopTextView.text = datetime.toString()
        holder.categoryTextView.text = record.category
        holder.amountTextView.text = "SGD " + record.amount.toString()

        if (record.status == "Approved"){
            holder.statusTextView.setBackgroundResource(green_approved)
        } else if (record.status == "Pending"){
            holder.statusTextView.setBackgroundResource(orange_pending)
        } else if (record.status == "Rejected"){
            holder.statusTextView.setBackgroundResource(red_rejected)
        } else if (record.status == "Paid"){
            holder.statusTextView.setBackgroundResource(blue_paid)
        }

        holder.statusTextView.text = record.status.toString()

    }

    /**
     * Function to get the size of claimList
     * @return claimList.size
     */
    override fun getItemCount(): Int{
        return claimList.size
    }

}