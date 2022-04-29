package edu.singaporetech.hrapp.leave.main

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.firestore.FirebaseFirestore
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentScheduleBinding
import edu.singaporetech.hrapp.leave.database.LeaveRecordViewModel
import org.threeten.bp.LocalDate
import java.util.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.leave.SwipeGesture
import edu.singaporetech.hrapp.leave.record.*

/**
 * Class for the Schedule Fragment to display on the UI
 */
class ScheduleFragment : Fragment(R.layout.fragment_schedule) {
    val TAG = this::class.java.simpleName

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    lateinit var pendingLeaves : MutableList<LeaveRecord>

    private var layoutManagerPending: RecyclerView.LayoutManager? = null
    private lateinit var mViewModelPending: LeaveRecordViewModel
    private lateinit var recyclerViewPending: RecyclerView

    private var layoutManagerApproved: RecyclerView.LayoutManager? = null
    private lateinit var mViewModelApproved: LeaveRecordViewModel
    private lateinit var recyclerViewApproved: RecyclerView

    /**
     * Creates the interface for the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return binding.root
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Runs code inside function upon view being created
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewPending = binding.pendingLeavesRec
        layoutManagerPending = LinearLayoutManager(activity)
        recyclerViewPending?.layoutManager = layoutManagerPending

        recyclerViewApproved = binding.approvedLeavesRec
        layoutManagerApproved = LinearLayoutManager(activity)
        recyclerViewApproved?.layoutManager = layoutManagerApproved

        activity?.title = "Leaves Schedule"

        markDateCalendar()
        getPendingLeaves()
    }

    /**
     * Gets the pending leaves in the firebase
     */
    private fun getPendingLeaves() {

        val adapterPending = LeaveSchedulePendingAdapter()
        recyclerViewPending.adapter = adapterPending

        val adapterApproved = LeaveScheduleApprovedAdapter()
        recyclerViewApproved.adapter = adapterApproved

        val leaveObserverPending = Observer<List<LeaveRecord>> { leaves ->
            adapterPending.setData(leaves as ArrayList<LeaveRecord>)
        }

        val leaveObserverApproved = Observer<List<LeaveRecord>> { leaves ->
            adapterApproved.setData(leaves as ArrayList<LeaveRecord>)
        }

        mViewModelPending = ViewModelProvider(this).get(LeaveRecordViewModel::class.java)
        mViewModelPending.getleaveRecord.observe(viewLifecycleOwner, leaveObserverPending)

        mViewModelApproved = ViewModelProvider(this).get(LeaveRecordViewModel::class.java)
        mViewModelApproved.getleaveRecord.observe(viewLifecycleOwner, leaveObserverApproved)

        // Enables swiping for recyclerview items
        val swipeGesture = object : SwipeGesture(requireContext()) {

            /**
             * Movement for swiping items in recyclerview
             * @param recyclerView
             * @param viewHolder
             * @return run
             */
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {

                return run {
                    val swipeFlags = ItemTouchHelper.LEFT
                    makeMovementFlags(0, swipeFlags)
                }
            }

            /**
             * Function for trigger action upon item being swiped
             * @param viewHolder
             * @param direction
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        showDialog("", adapterPending, viewHolder, viewHolder.absoluteAdapterPosition)
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(recyclerViewPending)

        adapterPending.setOnItemClickListener(object: LeaveSchedulePendingAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                pendingLeaves = adapterPending.getData()
                val type = pendingLeaves[position].type
                val from = pendingLeaves[position].from
                val to = pendingLeaves[position].to
                val doa = pendingLeaves[position].doa
                val daytype = pendingLeaves[position].daytype
                val remark = pendingLeaves[position].remarks
                val file = pendingLeaves[position].file
                val status = pendingLeaves[position].status

                // bundles to transfer data for between fragments
                val bundle = Bundle()
                bundle.putString("type", type)
                bundle.putString("from", from)
                bundle.putString("to", to)
                bundle.putString("doa", doa.toString())
                bundle.putString("daytype", daytype)
                bundle.putString("remark", remark)
                bundle.putString("file", file)
                bundle.putString("status", status)

                val fragment = LeaveDetailFragment()
                fragment.arguments = bundle

                (activity as MainActivity).replaceFragment(fragment)

            }
        })

        adapterApproved.setOnItemClickListener(object: LeaveScheduleApprovedAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                pendingLeaves = adapterPending.getData()
                val type = pendingLeaves[position].type
                val from = pendingLeaves[position].from
                val to = pendingLeaves[position].to
                val doa = pendingLeaves[position].doa
                val daytype = pendingLeaves[position].daytype
                val remark = pendingLeaves[position].remarks
                val file = pendingLeaves[position].file
                val status = pendingLeaves[position].status

                // bundles to transfer data for between fragments
                val bundle = Bundle()
                bundle.putString("type", type)
                bundle.putString("from", from)
                bundle.putString("to", to)
                bundle.putString("doa", doa.toString())
                bundle.putString("daytype", daytype)
                bundle.putString("remark", remark)
                bundle.putString("file", file)
                bundle.putString("status", status)

                val fragment = LeaveDetailFragment()
                fragment.arguments = bundle
                (activity as MainActivity).replaceFragment(fragment)

            }
        })
    }

    /**
     * Function that shows a dialog upon cancellation of pending leave
     * @param title
     * @param adapter
     * @param viewHolder
     * @param pos
     */
    private fun showDialog(title: String, adapter : LeaveSchedulePendingAdapter, viewHolder: RecyclerView.ViewHolder, pos: Int) {
        pendingLeaves = adapter.getData()
        val dialog = MaterialDialog(requireContext())
            .title(text = "Cancel Leave")
            .message(text = "Are you sure you want to cancel request of Date ${pendingLeaves[pos].from}?")
            .positiveButton(text= "Agree")
            .negativeButton(text = "Disagree")
            .icon(R.drawable.exclamation_mark)
            .positiveButton(text = "Agree") { dialog ->

                markDateCalendar()

                val daysDiff = LeaveUtils.getFullDays(pendingLeaves[pos].from, pendingLeaves[pos].to, pendingLeaves[pos].daytype)
                mViewModelPending.refundLeave(daysDiff,pendingLeaves[pos].type)
                // delete from database
                mViewModelPending.removeLeave(pendingLeaves[pos].id)
                // delete from recyclerview
                adapter.deleteItem(viewHolder.absoluteAdapterPosition)

                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                fragmentTransaction.replace(R.id.main_layout, LeaveFragment()).addToBackStack("schedule")
                fragmentTransaction.commit()

            }
            .negativeButton {
                val dialog = MaterialDialog(requireContext())
                // keep list intact
                adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                dialog.dismiss()
            }

        dialog.show()
    }

    /**
     * Set event for the calendar to mark in the schedule fragment
     * @param dateList
     * @param color
     */
    fun setEvent(dateList: List<String?>, color: String) {
        val localDateList: MutableList<LocalDate> = ArrayList()
        for (string in dateList) {
            val calendar: LocalDate? = getLocalDate(string)
            if (calendar != null) {
                localDateList.add(calendar)
            }
        }
        val datesLeft: MutableList<CalendarDay> = ArrayList()
        val datesCenter: MutableList<CalendarDay> = ArrayList()
        val datesRight: MutableList<CalendarDay> = ArrayList()
        val datesIndependent: MutableList<CalendarDay> = ArrayList()
        for (localDate in localDateList) {
            var right = false
            var left = false
            for (day1 in localDateList) {
                if (localDate.isEqual(day1.plusDays(1))) {
                    left = true
                }
                if (day1.isEqual(localDate.plusDays(1))) {
                    right = true
                }
            }
            if (left && right) {
                datesCenter.add(CalendarDay.from(localDate))
            } else if (left) {
                datesLeft.add(CalendarDay.from(localDate))
            } else if (right) {
                datesRight.add(CalendarDay.from(localDate))
            } else {
                datesIndependent.add(CalendarDay.from(localDate))
            }
        }
        if (color == "Beige") {
            setDecor(datesCenter, R.drawable.p_center)
            setDecor(datesLeft, R.drawable.p_left)
            setDecor(datesRight, R.drawable.p_right)
            setDecor(datesIndependent, R.drawable.p_independent)
        }else {
            setDecor(datesCenter, R.drawable.a_center)
            setDecor(datesLeft, R.drawable.a_left)
            setDecor(datesRight, R.drawable.a_right)
            setDecor(datesIndependent, R.drawable.a_independent)
        }
    }

    /**
     * Function that draws the marking on the calendar
     * @param calendarDayList
     * @param drawable
     */
    fun setDecor(calendarDayList: List<CalendarDay?>?, drawable: Int) {
        binding.leaveCalendar.addDecorators(
            getActivity()?.let {
                EventDecorator(
                    it, drawable, calendarDayList as List<CalendarDay>?
                )
            }
        )
    }

    /**
     * Function that converts the date parsed to a local date
     * @param date
     * return LocalDate?
     */
    private fun getLocalDate(date: String?): LocalDate? {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return try {
            val input: Date = sdf.parse(date)
            val cal = Calendar.getInstance()
            cal.time = input
            LocalDate.of(
                cal[Calendar.YEAR],
                cal[Calendar.MONTH] + 1,
                cal[Calendar.DAY_OF_MONTH]
            )
        } catch (e: NullPointerException) {
            null
        } catch (e: ParseException) {
            null
        }
    }

    /**
     * Function that gets the date range and parses it into the calendar
     * @param startDateString
     * @param enddateString
     * @return dates
     */
    private fun getDates(startDateString: String, enddateString: String): List<String> {
        val dates = ArrayList<String>()
        val df1: DateFormat = SimpleDateFormat("dd.MM.yyyy")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df1.parse(startDateString)
            date2 = df1.parse(enddateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        while (!cal1.after(cal2)) {
            val formatted: String = df1.format(cal1.time)
            dates.add(formatted)
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

    /**
     * Function that marks the dates on the calendar based on the data stored in the firebase
     */
    private fun markDateCalendar() {
        val db = FirebaseFirestore.getInstance()
            db.collection("leaves")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (document in it.result!!) {
                            if (document.data.getValue("status") as String == "Pending") {
                                val startDateString: String =
                                    document.data.getValue("from") as String
                                val endDateString: String = document.data.getValue("to") as String
                                setEvent(getDates(startDateString, endDateString), "Beige");
                            } else if (document.data.getValue("status") as String == "Approved") {
                                val startDateString: String =
                                    document.data.getValue("from") as String
                                val endDateString: String = document.data.getValue("to") as String
                                setEvent(getDates(startDateString, endDateString), "Green");
                            }
                        }
                    }
                }
    }
}