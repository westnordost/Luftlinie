package de.westnordost.luftlinie

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.westnordost.luftlinie.location.LocationState

class MainViewModel : ViewModel() {
    val locationState = MutableLiveData<LocationState?>(null)
    val currentLocation = MutableLiveData<Location?>(null)
    val destinationLocation = MutableLiveData<Location?>(null)
}