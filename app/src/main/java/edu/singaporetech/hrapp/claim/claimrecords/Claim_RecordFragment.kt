package edu.singaporetech.hrapp.claim.claimrecords

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.claimsmain.ClaimMainFragment
import edu.singaporetech.hrapp.claim.database.ClaimRecordViewModel
import edu.singaporetech.hrapp.databinding.FragmentClaimsRecordsBinding
import java.text.SimpleDateFormat

/**
 * Class for the ClaimRecordFragment to display claims records with a recyclerview
 * @return Fragment(R.layout.fragment_claims_records)
 */
class Claim_RecordFragment : Fragment(R.layout.fragment_claims_records) {

    private var _binding: FragmentClaimsRecordsBinding? = null
    private val binding get() = _binding!!

    lateinit var recordArrayList: MutableList<Records>

    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var mViewModel: ClaimRecordViewModel

    private lateinit var recyclerView: RecyclerView

    var status : String = "0"
    var category : String = "0"
    var startDateOfPurchase : String = "0"
    var endDateOfPurchase : String = "0"

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClaimsRecordsBinding.inflate(inflater, container, false)
        activity?.setTitle("Claim Record")
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).replaceFragment(ClaimMainFragment())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        recyclerView = binding.recyclerViewRecordList
        layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        getData()


        // Initialize Status Spinner
        val status_spinner: Spinner = binding.statusSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(requireContext(), R.array.status_array, android.R.layout.simple_spinner_item)
            .also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                status_spinner.adapter = adapter
            }

        status_spinner.setSelection(0, false)

        status_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "get data")
                getData()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    1 -> status = "Pending"
                    2 -> status = "Approved"
                    3 -> status = "Paid"
                    4 -> status = "Rejected"
                }
                Log.d(TAG, "filter data")
                filterData(status, category, startDateOfPurchase, endDateOfPurchase)
            }
        }

        // Initialize Category Spinner
        val category_spinner: Spinner = binding.categorySpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cmcategory_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            category_spinner.adapter = adapter
        }

        category_spinner.setSelection(0, false)

        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    1 -> category = "Transport"
                    2 -> category = "Medical"
                    3 -> category = "Allowance"
                }

                filterData(status, category, startDateOfPurchase, endDateOfPurchase)
            }
        }

        // Initialize Datepicker
        val editTextDateRangePicker = binding.editTextDatePickerRange
        editTextDateRangePicker.setRawInputType(InputType.TYPE_NULL)
        editTextDateRangePicker.setOnClickListener{
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            //create the picker, irrespectively of the builder
            val picker = builder.build()
            //Show the picker
            picker.show(requireFragmentManager(), picker.toString())
            //When dialog is cancelled
            picker.addOnCancelListener {
                Log.d("DatePicker Activity", "Dialog was cancelled")
            }
            // Date range picker the epoch values would be passed as a Pair object containing two Longs
            picker.addOnPositiveButtonClickListener {
                Log.d("DatePicker Activity", "Date String = ${picker.headerText}::  Date epoch values::${it.first}:: to :: ${it.second}")
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                var firstDT = dateFormat.format(it.first)
                val secondDT = dateFormat.format(it.second)
                startDateOfPurchase = it.first.toString()
                endDateOfPurchase = it.second.toString()
                filterData(status, category, startDateOfPurchase, endDateOfPurchase)
                editTextDateRangePicker.setText(firstDT.toString() + " - " + secondDT.toString())

            }
        }

        binding.resetButton.setOnClickListener {
            status_spinner.setSelection(0)
            category_spinner.setSelection(0)
            editTextDateRangePicker.setText("")
            status = "0"
            category = "0"
            startDateOfPurchase = "0"
            endDateOfPurchase = "0"
            getData()
        }
    }

    /**
     * Function that puts items on the recyclerview for the ClaimRecord fragment
     */
    private fun getData() {
        val adapter = Claim_RecordAdapter()
        recyclerView.adapter = adapter

        val claimObserver = Observer<List<Records>> { claims ->
            // Updates the UI
            adapter.setData(claims as ArrayList<Records>)
        }

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)
        mViewModel.getClaimRecord.observe(viewLifecycleOwner, claimObserver)


        adapter.setOnItemClickListener(object : Claim_RecordAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                recordArrayList = adapter.getData()
                val id = recordArrayList[position].id
                val amount = recordArrayList[position].amount
                val category = recordArrayList[position].category
                val dop = recordArrayList[position].dop.toString()
                val status = recordArrayList[position].status
                val receiptno = recordArrayList[position].receiptno
                val remarks = recordArrayList[position].remarks
                val uploadedfile = recordArrayList[position].uploadedfile
                // Bundles to transfer data for between fragments
                val bundle = Bundle()
                bundle.putString("id", id.toString())
                bundle.putString("amount", amount.toString())
                bundle.putString("category", category)
                bundle.putString("dop", dop)
                bundle.putString("status", status)
                bundle.putString("receiptno", receiptno)
                bundle.putString("remarks", remarks)
                bundle.putString("uploadedfile", uploadedfile)
                val fragment = Claim_DetailFragment()
                fragment.arguments = bundle

                (activity as MainActivity).replaceFragment(fragment)
            }
        })
    }

    /**
     * Function that enables the user to filter based on category, date or status
     * @param status
     * @param category
     * @param startDateOfPurchase
     * @param endDateOfPurchase
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterData(status: String, category: String, startDateOfPurchase: String, endDateOfPurchase: String) {

        val adapter = Claim_RecordAdapter()
        recyclerView.adapter = adapter

        val claimObserver = Observer<List<Records>> { claims ->
            // Update the UI
            adapter.setData(claims as ArrayList<Records>)
        }

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)
        mViewModel.filterClaim(status,category,startDateOfPurchase, endDateOfPurchase).observe(viewLifecycleOwner, claimObserver)

        adapter.setOnItemClickListener(object : Claim_RecordAdapter.onItemClickListener {

            override fun onItemClick(position: Int) {
                recordArrayList = adapter.getData()
                val id = recordArrayList[position].id
                val amount = recordArrayList[position].amount
                val category = recordArrayList[position].category
                val dop = recordArrayList[position].dop.toString()
                val status = recordArrayList[position].status
                val receiptno = recordArrayList[position].receiptno
                val remarks = recordArrayList[position].remarks
                val uploadedfile = recordArrayList[position].uploadedfile

                // Bundles to transfer data for between fragments
                val bundle = Bundle()
                bundle.putString("id", id.toString())
                bundle.putString("amount", amount.toString())
                bundle.putString("category", category)
                bundle.putString("dop", dop)
                bundle.putString("status", status)
                bundle.putString("receiptno", receiptno)
                bundle.putString("remarks", remarks)
                bundle.putString("uploadedfile", uploadedfile)

                val fragment = Claim_DetailFragment()
                fragment.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                    ?.addToBackStack("record")?.commit()
                (activity as MainActivity).replaceFragment(fragment)
            }
        })
    }

    companion object {
        private val TAG = "Spinner"
    }
}