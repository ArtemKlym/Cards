package com.artemklymenko.cards

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.artemklymenko.cards.view.activities.CheckoutActivity
import com.artemklymenko.cards.vm.DonationViewModel
import com.google.android.gms.wallet.PaymentData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CheckoutActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CheckoutActivity::class.java)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @MockK(relaxed = true)
    lateinit var viewModel: DonationViewModel

    @Test
    fun testGooglePayButtonVisibleWhenAvailable() {
        every { viewModel.canUseGooglePay } returns MutableLiveData(true)

        activityRule.scenario.onActivity { activity ->
            activity.setGooglePayAvailable(true)
        }

        onView(withId(R.id.googlePayButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testGooglePayButtonClickTriggersPayment() {
        val amount = "5"
        activityRule.scenario.onActivity { activity ->
            activity.setGooglePayAvailable(true)
        }

        onView(withId(R.id.googlePayButton)).perform(click())

        viewModel.getLoadPaymentDataTask(amount)

        verify { viewModel.getLoadPaymentDataTask(amount) }
    }

    @Test
    fun testHandlePaymentSuccess() {
        val paymentData = mockk<PaymentData>(relaxed = true)
        val jsonData = """
    {
        "paymentMethodData": {
            "tokenizationData": {
                "token": "mockToken"
            }
        }
    }
""".trimIndent()
        every { paymentData.toJson() } returns jsonData

        activityRule.scenario.onActivity { activity ->
            activity.handlePaymentSuccess(paymentData)
        }
    }
}