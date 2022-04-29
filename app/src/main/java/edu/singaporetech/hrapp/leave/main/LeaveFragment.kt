package edu.singaporetech.hrapp.leave.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import edu.singaporetech.hrapp.MainActivity
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.databinding.FragmentLeaveBinding
import edu.singaporetech.hrapp.leave.apply.LeaveApplicationFragment

/**
 * Class for the Main page of the Leave Fragment
 * @return Fragment(R.layout.fragment_leave)
 */
class LeaveFragment : Fragment(R.layout.fragment_leave) {

    private var _binding: FragmentLeaveBinding? = null
    private val binding get() = _binding!!

    var tabTitle = arrayOf("Overview", "Schedule")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLeaveBinding.inflate(inflater, container, false)

        val pager = binding.viewPager
        var tl = binding.tabLayout
        pager.adapter = LeaveFragmentAdapter(childFragmentManager, lifecycle)

        val fragmentManager  = requireActivity().supportFragmentManager
        val tag = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name
        if (tag.equals("schedule"))
        {
            pager.setCurrentItem(1)
        }


        TabLayoutMediator(tl, pager) {
                tab, position ->
            tab.text = tabTitle[position]
        }.attach()

        setHasOptionsMenu(true)


        activity?.title = "Leaves Overview"
        return binding.root
    }

    /**
     * Function that enables action menu to be created
     * @param menu
     * @param inflater
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.leaves_menu, menu)
    }

    /**
     * Function to start leave application fragment
     * @param item
     * @return Boolean
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        if (id == R.id.applyleaves_Button) {
            (activity as MainActivity).replaceFragment(LeaveApplicationFragment())
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Function to execute upon view being created
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Function to avoid memory leaks
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}