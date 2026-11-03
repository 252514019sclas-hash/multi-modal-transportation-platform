package com.simats.multimodaltransportationrecommendation.data.model.payment

import com.google.gson.annotations.SerializedName

data class PaymentMethodItem(
    @SerializedName("payment_id")
    val paymentId: Int,
    @SerializedName("method_type")
    val methodType: String, // "card", "upi", "wallet"
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String? = null,
    @SerializedName("is_default")
    val isDefault: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class PaymentMethodsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("payment_methods")
    val paymentMethods: List<PaymentMethodItem>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class AddPaymentMethodRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("method_type")
    val methodType: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("subtitle")
    val subtitle: String? = null
)

data class DeletePaymentMethodRequest(
    @SerializedName("payment_id")
    val paymentId: Int,
    @SerializedName("user_id")
    val userId: Int
)

