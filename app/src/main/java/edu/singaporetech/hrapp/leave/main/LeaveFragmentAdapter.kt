package edu.singaporetech.hrapp.leave.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Set layout tabs for leave fragment
 * @param fm
 * @param lifecycle
 * @return FragmentStateAdapter(fm, lifecycle)
 */
class LeaveFragmentAdapter(fm:FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    /**
     * Function that returns number of tabs
     * @return Int
     */
    override fun getItemCount(): Int {
        // return 2 tabs
        return 2
    }

    /**
     * Function that creates the fragment with tabs specified
     * @param position
     * @return Fragment
     */
    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> {return OverviewFragment()
            }
            1 -> {return ScheduleFragment()
            }
            else -> {return OverviewFragment()
            }
        }
    }
}