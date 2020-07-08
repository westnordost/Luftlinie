package de.westnordost.luftlinie.geocoding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** Stores the geocoding results and state  */
class GeocodingViewModel(private val geocoder: NominatimGeocoder) : ViewModel(),
    CoroutineScope by CoroutineScope(Dispatchers.Main)
{
    private val _results = MutableLiveData<List<GeocodingResult>?>()
    private val _isSearching = MutableLiveData<Boolean>(false)

    val results: LiveData<List<GeocodingResult>?> get() = _results
    val isSearching: LiveData<Boolean> get() = _isSearching

    fun search(query: String) {
        launch {
            _isSearching.value = true
            _results.value = geocoder.search(query)
            _isSearching.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}