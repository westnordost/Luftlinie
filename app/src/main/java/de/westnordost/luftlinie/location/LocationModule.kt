package de.westnordost.luftlinie.location

import android.content.Context
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.view.WindowManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val locationModule = module {

    factory { (callback: ((Location) -> Unit)) ->
        FineLocationManager(
            androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager,
            callback
        )
    }

    factory { (callback: (rotation: Float, tilt: Float) -> Unit) ->
        Compass(
            androidContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager,
            (androidContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay,
            callback
        )
    }
}