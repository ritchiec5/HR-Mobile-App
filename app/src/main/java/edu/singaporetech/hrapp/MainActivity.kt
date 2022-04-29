package edu.singaporetech.hrapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import edu.singaporetech.hrapp.attendance.AttendanceFragment
import edu.singaporetech.hrapp.claim.claimsmain.ClaimMainFragment
import edu.singaporetech.hrapp.leave.main.LeaveFragment


class MainActivity : AppCompatActivity() {

    /**
     * MainActivity that contains fragments of the 3 components: Leaves, Attendance and Claims
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navBar = findViewById<ChipNavigationBar>(R.id.nav_bar)
        replaceFragment(AttendanceFragment())

        navBar.setItemSelected(R.id.home_icon)

        navBar.setOnItemSelectedListener () { id ->

            when (id) {
                R.id.home_icon -> {
                    replaceFragment(AttendanceFragment())
                }
                R.id.claims_icon -> {
                    replaceFragment(ClaimMainFragment())
                }
                R.id.leaves_icon -> {
                    replaceFragment(LeaveFragment())
                }
            }
        }
    }

    /**
     * Function to navigate between fragments. I.E swap screens
     * @param fragment target fragment that is supposed to be replaced
     */
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
        fragmentTransaction.replace(R.id.main_layout, fragment).addToBackStack(null)
        fragmentTransaction.commit()
    }
}