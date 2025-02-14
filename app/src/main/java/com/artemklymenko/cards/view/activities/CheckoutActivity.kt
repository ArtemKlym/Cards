package com.artemklymenko.cards.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityCheckoutBinding
import com.artemklymenko.cards.utils.PaymentsUtil
import com.artemklymenko.cards.vm.DonationViewModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {

    private val viewModel: DonationViewModel by viewModels()
    private lateinit var googlePayButton: PayButton
    private var amount = "1"

    private val paymentDataLauncher: ActivityResultLauncher<Task<PaymentData>> =
        registerForActivityResult(GetPaymentDataResult()) { result ->
            val statusCode = result.status.statusCode
            when (statusCode) {
                CommonStatusCodes.SUCCESS -> result.result?.let { handlePaymentSuccess(it) }
                CommonStatusCodes.DEVELOPER_ERROR -> handleError(statusCode, result.status.statusMessage)
                else -> handleError(statusCode, "Unexpected non-API exception when trying to deliver the task result to an activity!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeUi()

        viewModel.canUseGooglePay.observe(this, ::setGooglePayAvailable)
    }

    private fun initializeUi() {
        val layoutBinding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)

        amount = intent.getStringExtra("amount").toString()

        layoutBinding.tvDonationAmount.text = getString(R.string.your_amount, amount)
        layoutBinding.backButton.setOnClickListener {
            finish()
        }
        googlePayButton = layoutBinding.googlePayButton
        try {
            googlePayButton.initialize(
                ButtonOptions.newBuilder()
                    .setAllowedPaymentMethods(PaymentsUtil.allowedPaymentMethods.toString())
                    .build()
            )
            googlePayButton.setOnClickListener { requestPayment() }
        } catch (e: JSONException) {
            Log.e("googlePayButton", "Error: ", e)
        }
    }


    fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, "Google Pay status: Unavailable", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPayment() {
        val task: Task<PaymentData> = viewModel.getLoadPaymentDataTask(amount)
        task.addOnCompleteListener { paymentDataLauncher.launch(it) }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     */
    fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInfo = paymentData.toJson()

        try {
            val paymentMethodData = JSONObject(paymentInfo).getJSONObject("paymentMethodData")

            Toast.makeText(this, "Donation: $paymentMethodData", Toast.LENGTH_LONG).show()

            Log.d("Google Pay token", paymentMethodData.getJSONObject("tokenizationData").getString("token"))

          startActivity(Intent(this, CheckoutSuccessActivity::class.java))
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: ", e)
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e("loadPaymentData failed", "Error code: $statusCode, Message: $message")
    }
}