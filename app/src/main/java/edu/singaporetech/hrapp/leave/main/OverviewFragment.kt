package edu.singaporetech.hrapp.leave.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentOverviewBinding
import edu.singaporetech.hrapp.leave.database.LeaveRecordViewModel
import edu.singaporetech.hrapp.leave.record.LeaveOverviewAdapter
import edu.singaporetech.hrapp.leave.record.LeaveRecord
import edu.singaporetech.hrapp.leave.record.LeaveRecordFragment

/**
 * Class that Overview Fragment displays on the UI
 */
class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    lateinit var leaveData : MutableList<LeaveData>

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var mViewModel: LeaveRecordViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = "Leaves Overview"

        recyclerView = binding.listOverviewRecords
        layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager

        val adapter = LeaveOverviewAdapter()
        recyclerView.adapter = adapter

        val leaveObserver = Observer<List<LeaveRecord>> { leaves ->
            // Update the UI
            adapter.setData(leaves as ArrayList<LeaveRecord>)

        }

        val leaveDataObserver = Observer<Any> { data ->
            adapter.setLeaveData(data as ArrayList<LeaveData>)
            leaveData = adapter.getLeaveData()
            setData()
        }

        mViewModel = ViewModelProvider(this).get(LeaveRecordViewModel::class.java)
        mViewModel.getleaveRecord.observe(viewLifecycleOwner, leaveObserver)
        mViewModel.getLeaveData.observe(viewLifecycleOwner, leaveDataObserver)


        // On record button click
        binding.viewHistoryBtn.setOnClickListener {
            (activity as MainActivity).replaceFragment(LeaveRecordFragment())
        }
    }

    /**
     * Function that calculates the amount of leaves translatable to the progress bars
     * @param value
     * @param total
     * @return int
     */
    private fun calculateAmount(value: Int, total: Int): Int {
        return ((value.toDouble() / total) * 100).toInt()
    }

    /**
     * Function that sets the leave amount data onto the progress bars
     */
    private fun setData() {
        // set amount
        binding.totalLeavesValue.text = leaveData[0].total.toString()
        binding.totalBalanceValue.text = leaveData[0].balance.toString()
        binding.totalUsedValue.text = leaveData[0].used.toString()
        binding.totalPendingValue.text = leaveData[0].pending.toString()

        // get total for each type
        var totalAnnual = leaveData[0].annual.toInt()
        var totalSick = leaveData[0].sick.toInt()
        var totalCompassion = leaveData[0].compassion.toInt()
        var totalOthers = leaveData[0].others.toInt()

        // get left for each type
        var currentAnnual = leaveData[0].annualleft.toInt()
        var currentSick = leaveData[0].sickleft.toInt()
        var currentCompassion = leaveData[0].compassionleft.toInt()
        var currentOthers = leaveData[0].othersleft.toInt()

        // set text left
        binding.textViewAnnualLeft.text = "$currentAnnual/$totalAnnual left"
        binding.textViewSickLeft.text = "$currentSick/$totalSick left"
        binding.textViewCompassionLeft.text = "$currentCompassion/$totalCompassion left"
        binding.textViewOthersLeft.text = "$currentOthers/$totalOthers left"

        // set progress bars
        binding.progressBarAnnual.progress = calculateAmount(currentAnnual, totalAnnual)
        binding.progressBarSick.progress = calculateAmount(currentSick, totalSick)
        binding.progressBarCompassion.progress = calculateAmount(currentCompassion, totalCompassion)
        binding.progressBarOthers.progress = calculateAmount(currentOthers, totalOthers)
    }

    /**
     * Function to avoid memory leaks
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}