package edu.singaporetech.hrapp.leave.record

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.FirebaseStorageKtxRegistrar
import com.google.firebase.storage.ktx.storage
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentLeaveDetailBinding
import edu.singaporetech.hrapp.leave.database.LeaveRecordViewModel
import edu.singaporetech.hrapp.leave.main.LeaveUtils
import java.io.File
import java.util.*
import java.text.SimpleDateFormat
import android.util.Base64

/**
 * Class that displays the details for the individual item in the LeaveRecordFragment
 */
class LeaveDetailFragment : Fragment(R.layout.fragment_leave_detail) {

    private var _binding: FragmentLeaveDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLeaveDetailBinding.inflate(inflater, container, false)

        // Fetchs data obtained from the arguments
        val args = this.arguments
        val type = args?.get("type")
        val from = args?.get("from")
        val to = args?.get("to")
        val doa = args?.get("doa")
        val daytype = args?.get("daytype")
        val remark = args?.get("remark")
        val file = args?.get("file")
        val status = args?.get("status")

        //Gets base64 string to convert into image from database
        val imageBytes = Base64.decode(file.toString(), Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.detailImageView.setImageBitmap(decodedImage)

        val dateString = LeaveUtils.datesApplied(from.toString(), to.toString())

        val s : String = doa.toString()
        val requiredString = s.substring(s.indexOf("=") + 1, s.indexOf(","))

        val sdf = SimpleDateFormat("d MMMM yyyy h:mma", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8")
        val formattedDate = sdf.format(requiredString.toLong()*1000)

        binding.detailType.text = type.toString()
        binding.detailDatesApplied.text = dateString
        binding.applicationDate.text = formattedDate
        binding.detailDayType.text = daytype.toString()
        binding.detailRemarks.text = remark.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.detailStatus.text = status.toString()
        if (status.toString() == "Approved")
        {
            binding.detailStatus.getBackground()
                .setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
        }
        if (status.toString() == "Declined")
        {
            binding.detailStatus.getBackground()
                .setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }

        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                activity?.supportFragmentManager?.popBackStackImmediate()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        activity?.setTitle("Leave Details")
        return binding.root
    }
}