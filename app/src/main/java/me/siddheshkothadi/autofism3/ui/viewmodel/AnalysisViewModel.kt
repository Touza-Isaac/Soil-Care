package me.siddheshkothadi.autofism3.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.siddheshkothadi.autofism3.FishApplication
import me.siddheshkothadi.autofism3.model.UploadHistoryFish
import me.siddheshkothadi.autofism3.repository.FishRepository
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val app: FishApplication,
    private val fishRepository: FishRepository,
): ViewModel() {
    val uploadHistoryFish = mutableStateOf<UploadHistoryFish?>(null)

    fun getByImageUri(imageURL: String) {
        viewModelScope.launch {
            uploadHistoryFish.value = fishRepository.getFishByImageUri(imageURL)
        }
    }
}