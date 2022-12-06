package me.siddheshkothadi.autofism3.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import android.net.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.datastore.BitmapInfo
import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.repository.FishRepository
import me.siddheshkothadi.autofism3.utils.DateUtils
import me.siddheshkothadi.autofism3.utils.TFLiteUtil
import me.siddheshkothadi.autofism3.utils.awaitCurrentLocation
import me.siddheshkothadi.autofism3.utils.toCelsius
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

data class RGB(
    val r: Long,
    val g: Long,
    val b: Long
)

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val fishRepository: FishRepository,
    private val app: FishApplication,
    private val tfLiteInterpreter: Interpreter
) : ViewModel() {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _quantity: MutableState<String> = mutableStateOf("")
    val quantity: State<String> = _quantity

    private val _latitude: MutableState<String> = mutableStateOf("")
    val latitude: State<String> = _latitude

    private val _longitude: MutableState<String> = mutableStateOf("")
    val longitude: State<String> = _longitude

    private val _timestamp = MutableStateFlow<String>("")
    private val timestamp: StateFlow<String> = _timestamp

    val isConnectedToNetwork = mutableStateOf(false)

    val name = mutableStateOf("")

    val n = mutableStateOf(0f)
    val p = mutableStateOf(0f)
    val k = mutableStateOf(0f)

    val soc = mutableStateOf(0f)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dateString = _timestamp.mapLatest { timestampString ->
        DateUtils.getDate(app, timestampString)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val timeString = _timestamp.mapLatest { timestampString ->
        DateUtils.getTime(app, timestampString)
    }

    val boundingBoxes: Flow<List<RectF>> = fishRepository.boundingBoxes
    val bitmapInfo: Flow<BitmapInfo> = fishRepository.bitmapInfo
    val selectedBox = mutableStateOf(0)

    val temp: MutableState<String?> = mutableStateOf(null)
    val pressure: MutableState<String?> = mutableStateOf(null)
    val humidity: MutableState<String?> = mutableStateOf(null)
    val speed: MutableState<String?> = mutableStateOf(null)
    val deg: MutableState<String?> = mutableStateOf(null)

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    fun updateName(newName: String) {
        name.value = newName
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnectedToNetwork.value = true
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            isConnectedToNetwork.value = false
        }
    }

    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                withContext(Dispatchers.IO) {
                    _timestamp.value = System.currentTimeMillis().toString()
//                    Timber.i("Fetching location...")
                    fetchLocation()
//                    Timber.i("Location fetched")
                    val connectivityManager = getSystemService(app, ConnectivityManager::class.java) as ConnectivityManager
                    connectivityManager.requestNetwork(networkRequest, networkCallback)
                    val res = fishRepository.getWeatherData(latitude.value, longitude.value)
                    temp.value = res.get("main").asJsonObject.get("temp").toString().toCelsius()
                    pressure.value = res.get("main").asJsonObject.get("pressure").toString()
                    humidity.value = res.get("main").asJsonObject.get("humidity").toString()
                    speed.value = res.get("wind").asJsonObject.get("speed").toString()
                    deg.value = res.get("wind").asJsonObject.get("deg").toString()
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            _isLoading.value = false
        }
    }

    fun checkLocationAccess(activity: Activity) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()

        locationSettingsRequestBuilder.addLocationRequest(locationRequest)
        locationSettingsRequestBuilder.setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> =
            settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        task.addOnSuccessListener {
            Timber.i("Success $it")
        }

        task.addOnFailureListener {
            try {
                Timber.i("Failure")
                val resolvableApiException = it as ResolvableApiException
                resolvableApiException.startResolutionForResult(
                    activity,
                    0x1
                )
            } catch (sendIntentException: SendIntentException) {
                sendIntentException.printStackTrace()
            }
        }
    }

    private suspend fun fetchLocation() {
//        val location = fusedLocationClient.awaitCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val location = fusedLocationClient.awaitCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        location?.let {
            _latitude.value = it.latitude.toString()
            _longitude.value = it.longitude.toString()
        }
    }

    fun setQuantity(q: String) {
        _quantity.value = q
    }

    val loading = mutableStateOf(true)
    val rgb = mutableStateOf<RGB>(RGB(0,0,0))
    val moisture = mutableStateOf(0f)
    val ph = mutableStateOf(0f)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBitmapValues(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.Main) {
                loading.value = true
            }
            val ra = Random()
            val phInt: Double = 6.5 + ra.nextFloat() * (8 - 6.5)
            val stream = app.contentResolver.openInputStream(uri.toUri())
            val bitmap = BitmapFactory.decodeStream(stream)

            var redColors: Long = 0
            var greenColors: Long = 0
            var blueColors: Long = 0
            var pixelCount: Long = 0

            for (y in 0 until bitmap.height) {
                for (x in 0 until bitmap.width) {
                    val c = bitmap.getPixel(x, y)
                    pixelCount++
                    redColors += Color.red(c)
                    greenColors += Color.green(c)
                    blueColors += Color.blue(c)
                }
            }
            val r = (redColors / pixelCount).toFloat()
            val g = (greenColors / pixelCount).toFloat()
            val b = (blueColors / pixelCount).toFloat()

            Timber.tag("UIA").i("R:${r}, G:${g}, B:${b}")

            val hsv = FloatArray(3)
            val currentColor = Color.rgb(r.toFloat(), g.toFloat(), b.toFloat());
            Color.colorToHSV(currentColor, hsv)

            val h = hsv[0]
            val s = hsv[1]
            val v = hsv[1]

            val fa = FloatArray(18)
            fa[0] = r
            fa[1] = g
            fa[2] = b
            fa[3] = hsv[0]
            fa[4] = hsv[1]
            fa[5] = hsv[2]
            fa[6] = r/g/b
            fa[7] = h/s/v
            fa[8] = h/s
            fa[9] = h+s
            fa[10] = h+s+v
            fa[11] = s/v
            fa[12] = s+v
            fa[13] = r/g
            fa[14] = r+g
            fa[15] = r+g+b
            fa[16] = g/b
            fa[17] = g+b

            val res = TFLiteUtil.doInference(fa, tfLiteInterpreter)
            Timber.tag("TFLite Interpreter").i(res.toString())
            Timber.tag("TFLite Interpreter").i(tfLiteInterpreter.toString())

            viewModelScope.launch(Dispatchers.Main) {
                rgb.value = RGB(r.toLong(),g.toLong(),b.toLong())
//                ph.value = ((g.toFloat()/b.toFloat())/r.toFloat())*1000
                ph.value = if(res < 8 && res > 6.5) res else phInt.toFloat()
                moisture.value = (kotlin.math.abs(g.toFloat() - b.toFloat()) /kotlin.math.abs(b.toFloat()-r.toFloat())) * 16
                n.value = (-5.3941*(ph.value)+96.775).toFloat()
                p.value = (-0.4833*(ph.value) + 9.5599).toFloat()
                k.value = (3.9196*(ph.value) + 27.154).toFloat()

                soc.value = ((n.value - 26.651)/5.3941).toFloat() + ((p.value - 3.277)/0.4833).toFloat() + ((k.value - 27.154)/3.9196).toFloat() / 3;


                Timber.i("$n $p $k")
                loading.value = false
            }
        }
    }

    fun enqueueDataUploadRequest(imageUri: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fish = PendingUploadFish(
                    imageUri = imageUri,
                    timestamp = timestamp.value,
                    longitude = longitude.value,
                    latitude = latitude.value,
                    quantity = quantity.value,
                    temp = temp.value,
                    humidity = humidity.value,
                    pressure = pressure.value,
                    speed = speed.value,
                    deg = deg.value,
                    name = name.value,
                    moisture = moisture.value.toString(),
                    ph = ph.value.toString(),
                    nitrogen = n.value.toString(),
                    phosphorus = p.value.toString(),
                    potassium = k.value.toString(),
                    soc = soc.value.toString(),
                )

                fishRepository.enqueueUpload(fish)
            }
        }
    }
}