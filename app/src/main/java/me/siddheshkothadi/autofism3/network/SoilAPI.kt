package me.siddheshkothadi.autofism3.network

import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.*

interface SoilAPI {
    @Multipart
    @POST("/predict")
    suspend fun uploadData(
//        @Header("Authorization") bearerToken: String,
        @Part image: MultipartBody.Part,
        @Part("id") id: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("name") name: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("temperature") temp: RequestBody?,
        @Part("humidity") humidity: RequestBody?,
        @Part("ph") pH:  RequestBody?,
        @Part("moisture") moisture:  RequestBody?,
        @Part("nitrogen") nitrogen:  RequestBody?,
        @Part("phosphorus") phosphorus: RequestBody?,
        @Part("potassium") potassium: RequestBody?,
        @Part("soc") soc: RequestBody?,
    )

    @GET("/predictions/{id}")
    suspend fun getHistory(@Path("id") id: String): List<UploadHistoryFish>
}