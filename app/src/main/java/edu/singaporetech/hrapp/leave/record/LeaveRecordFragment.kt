package edu.singaporetech.hrapp.leave.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentLeaveRecordBinding
import edu.singaporetech.hrapp.leave.database.LeaveRecordViewModel
import edu.singaporetech.hrapp.leave.main.LeaveFragment

/**
 * Class that connects the Leaves Recyclerview to the LeaveRecordFragment
 */
class LeaveRecordFragment : Fragment(R.layout.fragment_leave_record) {

    private var _binding: FragmentLeaveRecordBinding? = null
    private val binding get() = _binding!!

    lateinit var leaveRecords : MutableList<LeaveRecord>

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var mViewModel: LeaveRecordViewModel

    private lateinit var recyclerView: RecyclerView

    var status : String = "0"
    var type : String = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaveRecordBinding.inflate(inflater, container, false)

        activity?.setTitle("Leave Records")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).replaceFragment(LeaveFragment())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        recyclerView = binding.listViewRecords
        layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager

        getData()

        val type_spinner: Spinner = binding.typeSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout

        ArrayAdapter.createFromResource(requireContext(), R.array.leave_type_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Apply the adapter to the spinner
                type_spinner.adapter = adapter
            }

        type_spinner.setSelection(0, false)

        type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                getData()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    1 -> type = "Annual"
                    2 -> type = "Sick"
                    3 -> type = "Compassion"
                    4 -> type = "Others"
                }
                filterLeaves(type, status)
            }
        }

        //Initializes status spinner
        val status_spinner: Spinner = binding.statusSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.leave_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            status_spinner.adapter = adapter
        }

        status_spinner.setSelection(0, false)

        status_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    1 -> status = "Pending"
                    2 -> status = "Approved"
                    3 -> status = "Declined"
                    4 -> status = "Status"
                }

                if (status != "Status")
                    filterLeaves(type, status)
                else
                    status_spinner.setSelection(0)
            }
        }
    }

    private fun getData() {

        val adapter = LeaveRecordAdapter()
        recyclerView.adapter = adapter

        val leaveObserver = Observer<List<LeaveRecord>> { leaves ->
            // Update the UI
            adapter.setData(leaves as ArrayList<LeaveRecord>)
        }

        mViewModel = ViewModelProvider(this).get(LeaveRecordViewModel::class.java)
        mViewModel.getleaveRecord.observe(viewLifecycleOwner, leaveObserver)


        adapter.setOnItemClickListener(object : LeaveRecordAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {

                leaveRecords = adapter.getData()
                val type = leaveRecords[position].type
                val from = leaveRecords[position].from
                val to = leaveRecords[position].to
                val doa = leaveRecords[position].doa
                val daytype = leaveRecords[position].daytype
                val remark = leaveRecords[position].remarks
                val file = leaveRecords[position].file
                val status = leaveRecords[position].status

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
     * Function that enables the user to filter leaves in the record
     * @param type
     * @param status
     */
    private fun filterLeaves(type: String, status: String) {

        val adapter = LeaveRecordAdapter()
        recyclerView.adapter = adapter

        val leaveObserver = Observer<List<LeaveRecord>> { leaves ->
            // Update the UI
            adapter.setData(leaves as ArrayList<LeaveRecord>)
        }

        mViewModel = ViewModelProvider(this).get(LeaveRecordViewModel::class.java)
        mViewModel.filterLeave(type, status).observe(viewLifecycleOwner, leaveObserver)


        adapter.setOnItemClickListener(object : LeaveRecordAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {

                leaveRecords = adapter.getData()
                val type = leaveRecords[position].type
                val from = leaveRecords[position].from
                val to = leaveRecords[position].to
                val doa = leaveRecords[position].doa
                val daytype = leaveRecords[position].daytype
                val remark = leaveRecords[position].remarks
                val file = leaveRecords[position].file
                val status = leaveRecords[position].status

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
}