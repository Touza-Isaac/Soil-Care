package me.siddheshkothadi.autofism3

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.preference.PreferenceManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.siddheshkothadi.autofism3.datastore.LocalDataStore
import me.siddheshkothadi.autofism3.detection.tflite.Classifier
import me.siddheshkothadi.autofism3.detection.tflite.DetectorFactory
import me.siddheshkothadi.autofism3.detection.tflite.YoloV5Classifier
import me.siddheshkothadi.autofism3.ui.Language
import me.siddheshkothadi.autofism3.ui.nav.Screen
import me.siddheshkothadi.autofism3.utils.FileUtil
import me.siddheshkothadi.autofism3.utils.getFileExtension
import me.siddheshkothadi.autofism3.utils.setAppLocale
import me.siddheshkothadi.autofism3.utils.updateAppLocale
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: FishApplication,
    private val localDataStore: LocalDataStore
) : ViewModel() {

    private val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
    val detector: MutableState<YoloV5Classifier?> = mutableStateOf(null)
    val isLoading = mutableStateOf(true)

    private val _imageUri = mutableStateOf<Uri?>(null)
    private val imageUri: State<Uri?>
        get() = _imageUri

    private val imageName = mutableStateOf<String?>(null)

    var selectedLanguage = mutableStateOf(Language.ENGLISH)
    val startDestination = mutableStateOf(Screen.SelectLanguage.route)

    init {
        Timber.i("Init Block")
        viewModelScope.launch(Dispatchers.Main) {
            Timber.i(localDataStore.bearerToken.first())
//            viewModelScope.launch(Dispatchers.Main) {
                isLoading.value = true
//            }
//            viewModelScope.launch(Dispatchers.Main) {
                checkLanguage()
//            }
//            detector.value = DetectorFactory.getDetector(
//                app.assets,
//                Constants.MODEL_FILE_PATH
//            )

//            viewModelScope.launch(Dispatchers.Main) {
                isLoading.value = false
//            }
        }
    }

    private fun checkLanguage() {
        val currentLanguage = sharedPref.getString(Constants.LANGUAGE_KEY, "")
        if (!currentLanguage.isNullOrBlank()) {
            // Language set
            startDestination.value = Screen.Capture.route
        }
    }

    fun onRadioButtonSelected(context: Context, language: Language, recreateActivity: () -> Unit) {
        selectedLanguage.value = language
        context.updateAppLocale(language.locale)
        recreateActivity()
    }

    fun onLanguageChosen(recreateActivity: () -> Unit) {
        setLanguage(selectedLanguage.value.locale)
        recreateActivity()
        startDestination.value = Screen.Capture.route
    }

    fun onLanguageSelected(language: Language, recreateActivity: () -> Unit) {
        setLanguage(language.locale)
        recreateActivity()
    }

    suspend fun setBitmap(b: Bitmap) {
        withContext(Dispatchers.IO) {
            localDataStore.setBitmap(b)
        }
    }

    suspend fun saveBoundingBoxes(listOfLocation: List<Classifier.Recognition>) {
        withContext(Dispatchers.IO) {
            localDataStore.setRecognitions(listOfLocation)
        }
    }

    private fun setLanguage(language: String) {
        with(sharedPref.edit()) {
            putString(Constants.LANGUAGE_KEY, language)
            commit()
        }
    }

    fun getEncodedImageUri(contentUri: Uri): String {
        val fileExtension = contentUri.getFileExtension(app)
        val newImageName = "image_${System.currentTimeMillis()}" + if(fileExtension.isNullOrBlank()) "" else ".${fileExtension}"
        imageName.value = newImageName

        val imageFile = FileUtil.createNewFile(app, newImageName)

        try {
            val oStream = FileUtil.getOutputStream(imageFile)
            val inputStream = FileUtil.getInputStream(app, contentUri)

            inputStream?.let {
                FileUtil.copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val generatedImageUri = FileUtil.generateUri(app, imageFile)
        _imageUri.value = generatedImageUri

        return FileUtil.encodedUri(generatedImageUri)
    }

    fun generateNewImageUri(): Uri {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        imageName.value = fileName
        val uri = FileUtil.generateUri(app, fileName)
        _imageUri.value = uri
        return uri
    }
}