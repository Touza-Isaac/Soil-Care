package me.siddheshkothadi.autofism3.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import me.siddheshkothadi.autofism3.model.Predictions
import me.siddheshkothadi.autofism3.model.SoilType

@Entity
data class UploadHistoryFishEntity(
    val _id: String? = "",
    val name: String = "",
    val device_id: String = "",
    val user_id: String = "",
    @PrimaryKey val image_url: String = "",
    val longitude: String,
    val latitude: String,
    val quantity: String?,
    val timestamp: String,
    // Temp
    var temp: String? = null,
    var pressure: String? = null,
    var humidity: String? = null,
    // Wind
    var speed : String? = null,
    var deg   : String?    = null,
    val moisture: String? = null,
    val confidence: String? = null,
    val soilType: String? = null,
    val ph: String? = null,
    val nitrogen: String? = null,
    val phosphorus: String? = null,
    val potassium: String? = null,
    val soc: String? = null,
)
