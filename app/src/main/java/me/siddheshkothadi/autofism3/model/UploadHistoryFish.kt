package me.siddheshkothadi.autofism3.model

import com.google.gson.annotations.SerializedName

data class UploadHistoryFish(
    @SerializedName("_id") val _id: String?,
    @SerializedName("device_id") val device_id: String = "",
    @SerializedName("user_id") val user_id: String = "",
    @SerializedName("name") val name: String,
    @SerializedName("image_url") val image_url: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("quantity") val quantity: String?,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("temperature") val temp: String? = null,
    @SerializedName("pressure") val pressure: String? = null,
    @SerializedName("humidity") val humidity: String? = null,
    @SerializedName("speed") val speed: String? = null,
    @SerializedName("deg") val deg: String? = null,
    @SerializedName("moisture") val moisture: String? = null,
    @SerializedName("predictions") val predictions: Predictions? = null,
    @SerializedName("ph") val ph: String? = null,
    @SerializedName("nitrogen") val nitrogen: String? = null,
    @SerializedName("phosphorus") val phosphorus: String? = null,
    @SerializedName("potassium") val potassium: String? = null,
    @SerializedName("soc") val soc: String? = null,
)

data class SoilType(
    @SerializedName("confidence") val confidence: String?,
    @SerializedName("type") val type: String?
)

data class Predictions(
    @SerializedName("soiltype") val soilType: SoilType?
)
