package com.artemklymenko.cards.utils

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object PaymentsUtil {
    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private val baseRequest = JSONObject()
        .put("apiVersion", 2)
        .put("apiVersionMinor", 0)

    /**
     * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
     *
     *
     * The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     *
     * TODO: Check with your gateway on the parameters to pass and modify them in Constants.java.
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * See [PaymentMethodTokenizationSpecification](https://developers.google.com/pay/api/android/reference/object.PaymentMethodTokenizationSpecification)
     */
    private val gatewayTokenizationSpecification: JSONObject =
        JSONObject()
            .put("type", "PAYMENT_GATEWAY")
            .put("parameters", JSONObject(Constants.PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS))

    /**
     * Card networks supported by your app and your gateway.
     *
     *
     * TODO: Confirm card networks supported by your app and gateway & update in Constants.java.
     *
     * @return Allowed card networks
     * See [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private val allowedCardNetworks = JSONArray(Constants.SUPPORTED_NETWORKS)

    /**
     * Card authentication methods supported by your app and your gateway.
     *
     *
     * TODO: Confirm your processor supports Android device tokens on your supported card networks
     * and make updates in Constants.java.
     *
     * @return Allowed card authentication methods.
     * See [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private val allowedCardAuthMethods = JSONArray(Constants.SUPPORTED_METHODS)

    /**
     * Describe your app's support for the CARD payment method.
     *
     *
     * The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * See [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    private fun baseCardPaymentMethod(): JSONObject =
        JSONObject()
            .put("type", "CARD")
            .put("parameters", JSONObject()
                .put("allowedAuthMethods", allowedCardAuthMethods)
                .put("allowedCardNetworks", allowedCardNetworks)
                .put("billingAddressRequired", true)
                .put("billingAddressParameters", JSONObject()
                    .put("format", "FULL")
                )
            )

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * See [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    private val cardPaymentMethod: JSONObject = baseCardPaymentMethod()
        .put("tokenizationSpecification", gatewayTokenizationSpecification)

    val allowedPaymentMethods: JSONArray = JSONArray().put(cardPaymentMethod)

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * See [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    fun isReadyToPayRequest(): JSONObject? =
        try {
            baseRequest
                .put("allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))
        } catch (e: JSONException) {
            null
        }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * See [MerchantInfo](https://developers.google.com/pay/api/android/reference/object.MerchantInfo)
     */
    private val merchantInfo: JSONObject =
        JSONObject().put("merchantName", "Example Merchant")

    /**
     * Creates an instance of [PaymentsClient] for use in an [Context] using the
     * environment and theme set in [Constants].
     *
     * @param context from the caller activity.
     */
    fun createPaymentsClient(context: Context): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(Constants.PAYMENTS_ENVIRONMENT)
            .build()

        return Wallet.getPaymentsClient(context, walletOptions)
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * See [TransactionInfo](https://developers.google.com/pay/api/android/reference/object.TransactionInfo)
     */
    private fun getTransactionInfo(price: String): JSONObject =
        JSONObject()
            .put("totalPrice", price)
            .put("totalPriceStatus", "FINAL")
            .put("countryCode", Constants.COUNTRY_CODE)
            .put("currencyCode", Constants.CURRENCY_CODE)

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * See [PaymentDataRequest](https://developers.google.com/pay/api/android/reference/object.PaymentDataRequest)
     */
    fun getPaymentDataRequest(priceLabel: String): JSONObject =
        baseRequest
            .put("allowedPaymentMethods", allowedPaymentMethods)
            .put("transactionInfo", getTransactionInfo(priceLabel))
            .put("merchantInfo", merchantInfo)
            .put("shippingAddressRequired", true)
            .put("shippingAddressParameters", JSONObject()
                .put("phoneNumberRequired", false)
                .put("allowedCountryCodes", JSONArray(listOf("US", "GB")))
            )
}