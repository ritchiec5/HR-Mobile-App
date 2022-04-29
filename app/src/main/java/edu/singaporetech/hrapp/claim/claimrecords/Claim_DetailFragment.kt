package edu.singaporetech.hrapp.claim.claimrecords

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.database.ClaimRecordViewModel
import edu.singaporetech.hrapp.claim.editclaims.Claim_EditFragment
import edu.singaporetech.hrapp.databinding.FragmentClaimsDetailBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Class for the ClaimDetailFragment
 * @return Fragment(R.layout.fragment_claims_detail)
 */
class Claim_DetailFragment : Fragment(R.layout.fragment_claims_detail) {
    private var _binding: FragmentClaimsDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var status: String
    lateinit var itemid: String
    lateinit var amount: String
    lateinit var category: String
    lateinit var dop: String
    lateinit var receiptno: String
    lateinit var remarks: String
    lateinit var uploadedfile : String
    private lateinit var mViewModel: ClaimRecordViewModel


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentClaimsDetailBinding.inflate(inflater, container, false)

        mViewModel = ViewModelProvider(this).get(ClaimRecordViewModel::class.java)

        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStackImmediate()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Fetches data from arguments
        val args = this.arguments
        itemid = args?.get("id").toString()
        amount = args?.get("amount").toString()
        category = args?.get("category").toString()
        dop = args?.get("dop").toString()
        status = args?.get("status").toString()
        receiptno = args?.get("receiptno").toString()
        remarks = args?.get("remarks").toString()
        uploadedfile = args?.get("uploadedfile").toString()
        args?.clear()

        // Change the status tag color
        if (status == "Approved"){
            binding.textViewStatus?.setBackgroundResource(R.color.green_approved)
        } else if (status == "Pending"){
            binding.textViewStatus?.setBackgroundResource(R.color.orange_pending)
        } else if (status == "Rejected"){
            binding.textViewStatus?.setBackgroundResource(R.color.red_rejected)
        } else if (status == "Paid"){
            binding.textViewStatus?.setBackgroundResource(R.color.blue_paid)
        }

        binding.textViewAmount.text = amount.toString()
        binding.textViewCategory.text = category.toString()

        // Format the date from epoch second
        var newdatetime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dop.toLong()), ZoneOffset.UTC)
        var formatter = DateTimeFormatter.ofPattern("EEE ,dd MMM yyyy")
        var formateddatetime = newdatetime.format(formatter)
        binding.textViewDoP.text = formateddatetime.toString()
        binding.textViewReceiptNumber.text = receiptno.toString()
        binding.textViewStatus.text = status.toString()

        // Decodes image from base64 string
        val imageBytes = Base64.decode(uploadedfile, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.imageViewUploadedFile.setImageBitmap(decodedImage)
        binding.textViewRemarks.text = remarks

        activity?.setTitle("Claim Detail")

        return binding.root
    }

    /**
     * Function that enables the options menu in this fragment
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Function that Inflates the menu
     * @param menu
     * @param inflater
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_claims_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Function that Prepares the option menu to edit or delete claim
     * @param menu
     */
    override fun onPrepareOptionsMenu(menu: Menu) {
        if(status != "Pending"){
            menu?.findItem(R.id.action_editclaim)?.isEnabled = false
            menu?.findItem(R.id.action_deleteclaim)?.isEnabled = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * OnClick Function that enables user to edit the current claim details
     * @param item
     * @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        //get item id to handle item clicks
        val id = item!!.itemId
        if(id == R.id.action_editclaim){
            // bundles to transfer data for between fragments
            val bundle = Bundle()
            bundle.putString("id", itemid)
            bundle.putString("status", status)
            bundle.putString("dop", dop)
            bundle.putString("category", category)
            bundle.putString("amount", amount)
            bundle.putString("receiptno", receiptno)
            bundle.putString("remarks", remarks)
            bundle.putString("uploadedfile", uploadedfile)

            val fragment = Claim_EditFragment()
            fragment.arguments = bundle

            (activity as MainActivity).replaceFragment(fragment)
        }

        if(id == R.id.action_deleteclaim){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete claim")
            builder.setMessage(R.string.message_delete_claims)

            builder.setPositiveButton(R.string.confirm_delete_claims) { dialog, which ->

                mViewModel.refundClaimData(binding.textViewAmount.text.toString().toDouble(),category)
                mViewModel.removeClaim(itemid)

                (activity as MainActivity).replaceFragment(Claim_RecordFragment())
            }

            builder.setNegativeButton(R.string.close_delete_claims) { dialog, which ->
                dialog.cancel()
            }

            builder.show()

        }

        return super.onOptionsItemSelected(item)
    }


    companion object {
        private val TAG = "FireStore"
    }
}