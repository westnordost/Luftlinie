package de.westnordost.luftlinie.location

import android.location.LocationListener
import android.os.Bundle

interface LocationUpdateListener : LocationListener {

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}
}
