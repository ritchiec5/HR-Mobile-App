package edu.singaporetech.hrapp.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import edu.singaporetech.hrapp.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters


/**
 * The instrumented blackbox test suite for the MainActivity.
 * Note that the ListActivity is not tested here and its associated view ids (listViewFourDigits,
 * switchGrid and resetDataButton) and are not included here. You should create your own test cases
 * for those.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // run in alphabet order of method name
@RunWith(AndroidJUnit4::class)
@LargeTest
class InstrumentedTest {
    // UiAutomator device
    lateinit var device: UiDevice

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun setupUiAutomator() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
        var waitButton = device.findObject(UiSelector().textContains("wait"))
        if (waitButton.exists()) {
            waitButton.click()
        }
    }

    @Test
    fun onStartMainActivity_uiMatches() {
        /** Login Test Cases */
        onView(withId(R.id.editTextUsername))
            .check(matches(isDisplayed())).perform(replaceText("marytan@hrcompany.com"))

        onView(withId(R.id.editTextPassword))
            .check(matches(isDisplayed())).perform(replaceText("password123"))

        onView(withId(R.id.logInButton)).perform(click())

        /** Attendance Fragment Test Cases */
        var resourceFound = waitForResourceId(R.id.dateTextView) // Wait for Attendance Fragment to load

        onView(withId(R.id.dateTextView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.location_textView))
            .check(matches(isDisplayed()))
        onView(withId(R.id.imageButton))
            .check(matches(isDisplayed()))
        onView(withId(R.id.radioGroup))
            .check(matches(isDisplayed()))
        onView(withId(R.id.radioWork))
            .check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.radioWFH))
            .check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.radioCustomerSite))
            .check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.googleMap))
            .check(matches(isDisplayed()))
        onView(withId(R.id.checkInButton))
            .check(matches(isDisplayed())).perform(click())

        /** Camera Activity Test Case */
//        onView(withId(R.id.image_capture_button))
//            .check(matches(isDisplayed())).perform(click())

        /** Facial Recognition Test Case */
//        resourceFound = waitForResource(R.id.retakeButton)
//        onView(withId(R.id.retakeButton))
//            .check(matches(isDisplayed()))
//        onView(withId(R.id.nextButton))
//            .check(matches(isDisplayed())).perform(click())
//        onView(withText(Matchers.equalTo("HELP"))).perform(click())

        /** Email Test Case */
        device.pressBack()
        resourceFound = waitForResourceId(R.id.helpButton)
        onView(withId(R.id.helpButton))
            .check(matches(isDisplayed())).perform(click())
        device.pressBack()

        /** Claims Test Case */
        try{
            onView(withId(R.id.claims_icon))
                .check(matches(isDisplayed())).perform(click())
            Thread.sleep(1000)
        }
        catch(_: Exception){}

//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext())
//        onView(withText("Scan A Receipt")).perform(click())
//        Thread.sleep(1000)
//
//        device.pressBack()
//
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext())
//        onView(withText("Manual Submission")).perform(click())
//        Thread.sleep(1000)
//
//        onView(withId(R.id.editTextAmount))
//            .check(matches(isDisplayed())).perform(replaceText("12"))
//
//        onView(withId(R.id.editTextNumberReceiptNum))
//            .check(matches(isDisplayed())).perform(replaceText("001"))
//
//        onView(withId(R.id.editTextRemarks))
//            .check(matches(isDisplayed())).perform(replaceText("Testing"))
//
//        onView(withId(R.id.buttonChooseFile))
//            .check(matches(isDisplayed())).perform(click())
//        Thread.sleep(2000)
//
//        device.pressBack()
//
//        device.pressBack()

//        Thread.sleep(2000)

//        onView(withId(R.id.buttonViewHistory))
//            .check(matches(isDisplayed())).perform(scrollTo(),click())
//        Thread.sleep(2000)


        /** Leaves Test Case */
        try{
            onView(withId(R.id.leaves_icon))
                .check(matches(isDisplayed())).perform(click())
            onView(withId(R.id.action_editclaim)).perform(click())

            // Click the item.
            onView(withText("Edit Claim"))
                .perform(click())

        }
        catch(_: Exception){}
        waitForResourceId(R.id.tab_layout)
        onView(withContentDescription("Schedule"))
            .check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.applyleaves_Button))
            .check(matches(isDisplayed())).perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.btnFull))
            .check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.btnAM))
            .check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.btnPM))
            .check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.LeaveReasonID))
            .check(matches(isDisplayed())).perform(replaceText("Test Reason"))


        onView(withId(R.id.uploadProofBtn))
            .check(matches(isDisplayed())).perform(click())
        Thread.sleep(2000)

        device.pressBack()

        onView(withId(R.id.submitLeaveAppBtn))
            .check(matches(isDisplayed())).perform(click())

        device.pressBack()
        device.pressBack()


        onView(withContentDescription("Overview"))
            .check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.viewHistoryBtn)).perform(scrollTo())
            .check(matches(isDisplayed())).perform(click())
        Thread.sleep(2000)

    }

    private fun waitForResourceId(resourceId: Int): Boolean {
        var found = false
        while(!found){
            try{
                onView(withId(resourceId))
                    .check(matches(isDisplayed()))
                found = true
                Thread.sleep(1000)
            }
            catch(_: Exception){
                Thread.sleep(1000)
            }
        }
        return true
    }

    private fun waitForContent(content: String): Boolean {
        var found = false
        while(!found){
            try{
                onView(withContentDescription(content))
                    .check(matches(isDisplayed()))
                found = true
                Thread.sleep(1000)
            }
            catch(_: Exception){
                Thread.sleep(1000)
            }
        }
        return true
    }

}