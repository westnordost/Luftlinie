package de.westnordost.luftlinie.geocoding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

/** Stores the geocoding results and state  */
class GeocodingViewModel(private val geocoder: NominatimGeocoder) : ViewModel() {
    private val _results = MutableLiveData<List<GeocodingResult>?>()
    private val _isSearching = MutableLiveData<Boolean>(false)
    private val _lastError = MutableLiveData<String?>()

    val results: LiveData<List<GeocodingResult>?> get() = _results
    val isSearching: LiveData<Boolean> get() = _isSearching
    val lastError: LiveData<String?> get() = _lastError

    fun search(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                _results.value = geocoder.search(query)
            } catch (e: Exception) {
                _lastError.value = e.localizedMessage
            }
            _isSearching.value = false
        }
    }
}