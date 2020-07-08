package de.westnordost.luftlinie.location

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

fun Context.isLocationOn(): Boolean = hasLocationPermission() && isLocationEnabled()

fun Context.isLocationEnabled(): Boolean =
    LocationManagerCompat.isLocationEnabled(this.getSystemService(LOCATION_SERVICE) as LocationManager)

fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun createLocationAvailabilityIntentFilter() = IntentFilter(LocationManager.MODE_CHANGED_ACTION)