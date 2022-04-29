package edu.singaporetech.hrapp.login


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import edu.singaporetech.hrapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class Test {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION"
        )

    @Test
    fun test() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.editTextUsername),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.RelativeLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("marytan@hrcompany.com"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.editTextPassword),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.RelativeLayout")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("password123"), closeSoftKeyboard())

        val appCompatButton = onView(
            allOf(
                withId(R.id.logInButton), withText("Login"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.RelativeLayout")),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())
        Thread.sleep(3000)

        val horizontalMenuItemView = onView(
            allOf(
                withId(R.id.claims_icon), withContentDescription("Claim"),
                childAtPosition(
                    allOf(
                        withId(R.id.nav_bar),
                        childAtPosition(
                            withId(R.id.main_layout),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        horizontalMenuItemView.perform(click())

        val horizontalMenuItemView2 = onView(
            allOf(
                withId(R.id.leaves_icon), withContentDescription("Leave"),
                childAtPosition(
                    allOf(
                        withId(R.id.nav_bar),
                        childAtPosition(
                            withId(R.id.main_layout),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        horizontalMenuItemView2.perform(click())

        val tabView = onView(
            allOf(
                withContentDescription("Schedule"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tab_layout),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val horizontalMenuItemView3 = onView(
            allOf(
                withId(R.id.claims_icon), withContentDescription("Claim"),
                childAtPosition(
                    allOf(
                        withId(R.id.nav_bar),
                        childAtPosition(
                            withId(R.id.main_layout),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        horizontalMenuItemView3.perform(click())

        val overflowMenuButton = onView(
            allOf(
                withContentDescription("More options"),
                childAtPosition(
                    childAtPosition(
                        withId(androidx.appcompat.R.id.action_bar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        overflowMenuButton.perform(click())

        val materialTextView = onView(
            allOf(
                withId(R.id.title), withText("Manual Submission"),
                childAtPosition(
                    childAtPosition(
                        withId(androidx.appcompat.R.id.content),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        val materialButton = onView(
            allOf(
                withId(R.id.buttonDate), withText("Select Date"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        materialButton.perform(scrollTo(), click())

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val appCompatSpinner = onView(
            allOf(
                withId(R.id.spinner1),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        appCompatSpinner.perform(scrollTo(), click())

        val appCompatCheckedTextView = onData(anything())
//            .inAdapterView(
//                childAtPosition(
//                    withClassName(`is`("android.widget.PopupWindow")),
//                    0
//                )
//            )
            .inRoot(isPlatformPopup())
            .atPosition(1)
        appCompatCheckedTextView.perform(click())

        val currencyEditText = onView(
            allOf(
                withId(R.id.editTextAmount),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        currencyEditText.perform(scrollTo(), replaceText("0.0"), closeSoftKeyboard())

//        val currencyEditText2 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.0"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText2.perform(closeSoftKeyboard())

        val currencyEditText3 = onView(
            allOf(
                withId(R.id.editTextAmount),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        currencyEditText3.perform(scrollTo(), replaceText("0.0"), closeSoftKeyboard())

//        val currencyEditText4 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.0"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText4.perform(closeSoftKeyboard())

        val currencyEditText5 = onView(
            allOf(
                withId(R.id.editTextAmount),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        currencyEditText5.perform(scrollTo(), replaceText("0.0"), closeSoftKeyboard())

//        val currencyEditText6 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.0"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText6.perform(closeSoftKeyboard())

//        val currencyEditText7 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.00"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                )
//            )
//        )
//        currencyEditText7.perform(scrollTo(), replaceText("0.0"), closeSoftKeyboard())

//        val currencyEditText8 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.0"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText8.perform(closeSoftKeyboard())

//        val currencyEditText9 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.00"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                )
//            )
//        )
//        currencyEditText9.perform(scrollTo(), click())

        val currencyEditText10 = onView(
            allOf(
                withId(R.id.editTextAmount),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        currencyEditText10.perform(scrollTo(), replaceText("1.00"), closeSoftKeyboard())

//        val currencyEditText11 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText(".00"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText11.perform(closeSoftKeyboard())

        val currencyEditText12 = onView(
            allOf(
                withId(R.id.editTextAmount),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        currencyEditText12.perform(scrollTo(), replaceText("1.00"), closeSoftKeyboard())

//        val currencyEditText13 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.001"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText13.perform(closeSoftKeyboard())

        val currencyEditText14 = onView(
            allOf(
                withId(R.id.editTextAmount),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        currencyEditText14.perform(scrollTo(), replaceText("0.10"), closeSoftKeyboard())

//        val currencyEditText15 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.010"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText15.perform(closeSoftKeyboard())

//        val currencyEditText16 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.10"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                )
//            )
//        )
//        currencyEditText16.perform(scrollTo(), replaceText("1.00"), closeSoftKeyboard())

//        val currencyEditText17 = onView(
//            allOf(
//                withId(R.id.editTextAmount), withText("0.100"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.ScrollView")),
//                        0
//                    ),
//                    6
//                ),
//                isDisplayed()
//            )
//        )
//        currencyEditText17.perform(closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.editTextNumberReceiptNum),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    8
                )
            )
        )
        appCompatEditText3.perform(scrollTo(), replaceText("555"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.editTextRemarks),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    10
                )
            )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("medical"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.editTextRemarks), withText("medical"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    10
                )
            )
        )
        appCompatEditText5.perform(scrollTo(), pressImeActionButton())

        val materialButton3 = onView(
            allOf(
                withId(R.id.buttonSubmit), withText("Submit"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        14
                    ),
                    1
                )
            )
        )
        materialButton3.perform(scrollTo(), click())


//        val dialogActionButton = onView(
//            allOf(
//                withText("Agree"),
//                childAtPosition(
//                    allOf(
//
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        dialogActionButton.perform(click())
//
//        val dialogActionButton2 = onView(
//            allOf(
//                withText("OK"),
//                childAtPosition(
//                    allOf(
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        dialogActionButton2.perform(click())


//        val bootstrapButton = onView(
//            allOf(
//                withId(R.id.buttonViewHistory), withText("View All"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("androidx.cardview.widget.CardView")),
//                        0
//                    ),
//                    2
//                )
//            )
//        )
//        bootstrapButton.perform(scrollTo(), click())
//
//        val appCompatSpinner2 = onView(
//            allOf(
//                withId(R.id.status_spinner),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.LinearLayout")),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner2.perform(click())
//
//        val appCompatCheckedTextView2 = onData(anything())
//            .inAdapterView(
//                childAtPosition(
//                    withClassName(`is`("android.widget.PopupWindow")),
//                    0
//                )
//            )
//            .atPosition(1)
//        appCompatCheckedTextView2.perform(click())
//
//        val appCompatSpinner3 = onView(
//            allOf(
//                withId(R.id.category_spinner),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.LinearLayout")),
//                        0
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        appCompatSpinner3.perform(click())
//
//        val appCompatCheckedTextView3 = onData(anything())
//            .inAdapterView(
//                childAtPosition(
//                    withClassName(`is`("android.widget.PopupWindow")),
//                    0
//                )
//            )
//            .atPosition(1)
//        appCompatCheckedTextView3.perform(click())
//
//        val materialButton4 = onView(
//            allOf(
//                withId(R.id.resetButton), withText("Reset"),
//                childAtPosition(
//                    childAtPosition(
//                        withClassName(`is`("android.widget.LinearLayout")),
//                        1
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton4.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}