package com.artemklymenko.cards.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artemklymenko.cards.utils.PaymentsUtil.createPaymentsClient
import com.artemklymenko.cards.utils.PaymentsUtil.getPaymentDataRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DonationViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private var paymentsClient: PaymentsClient = createPaymentsClient(application)

    private val _canUseGooglePay = MutableLiveData<Boolean>()

    val canUseGooglePay: LiveData<Boolean> = _canUseGooglePay

    /**
     * Creates a Task that starts the payment process with the transaction details included.
     *
     * @param priceLabel the price to show on the payment sheet.
     * @return a Task with the payment information.
     */
    fun getLoadPaymentDataTask(priceLabel: String?): Task<PaymentData> {
        val paymentDataRequestJson = getPaymentDataRequest(priceLabel!!)

        val request =
            PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }
}