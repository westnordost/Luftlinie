package de.westnordost.luftlinie.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import de.westnordost.luftlinie.MainViewModel
import de.westnordost.luftlinie.R
import de.westnordost.luftlinie.location.LocationState.*
import kotlinx.android.synthetic.main.fragment_destination.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

class DestinationFragment : Fragment(R.layout.fragment_destination) {

    private val mainModel: MainViewModel by sharedViewModel()
    private val fineLocationManager: FineLocationManager by inject { parametersOf(this::onLocationUpdate) }
    private val compass: Compass by inject { parametersOf(this::onCompassUpdate) }

    private val locationRequestFragment: LocationRequestFragment? get() =
        childFragmentManager.findFragmentByTag("LocationRequestFragment") as LocationRequestFragment?

    private val locationAvailabilityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onLocationAvailabilityUpdate()
        }
    }

    /* --------------------------------------- Lifecycle ---------------------------------------- */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(LocationRequestFragment(), "LocationRequestFragment")
                .commit()
        }

        mainModel.locationState.observe(this) { locationState ->
            if (locationState == ENABLED) {
                mainModel.locationState.value = SEARCHING
                startLocationUpdates()
            }
        }
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        if (childFragment is LocationRequestFragment) {
            if (mainModel.locationState.value == null) {
                childFragment.startRequest()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationStateView.setOnClickListener {
            locationRequestFragment?.startRequest()
        }

        updateLocationState(mainModel.locationState.value)
        mainModel.locationState.observe(viewLifecycleOwner) { updateLocationState(it) }

        updateCurrentLocation(mainModel.currentLocation.value)
        mainModel.currentLocation.observe(viewLifecycleOwner) { updateCurrentLocation(it) }

        updateDestinationLocation(mainModel.destinationLocation.value)
        mainModel.destinationLocation.observe(viewLifecycleOwner) { updateDestinationLocation(it) }
    }

    override fun onStart() {
        super.onStart()

        requireContext().registerReceiver(
            locationAvailabilityReceiver,
            createLocationAvailabilityIntentFilter()
        )

        if (mainModel.locationState.value == SEARCHING) {
            startLocationUpdates()
        }
    }

    override fun onResume() {
        super.onResume()
        compass.onResume()
    }

    override fun onPause() {
        super.onPause()
        compass.onPause()
    }

    private fun onCompassUpdate(rotation: Float, tilt: Float) {
        destinationPointerView.deviceRotation = rotation
    }

    private fun onLocationUpdate(location: Location) {
        compass.setLocation(location)
        mainModel.locationState.value = UPDATING
        mainModel.currentLocation.value = location
    }

    private fun onLocationAvailabilityUpdate() {
        mainModel.locationState.value = if (requireContext().isLocationEnabled()) ENABLED else ALLOWED
    }

    override fun onStop() {
        super.onStop()

        requireContext().unregisterReceiver(locationAvailabilityReceiver)

        stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        compass.onDestroy()
    }

    /* -------------------------------------- View updates -------------------------------------- */

    private fun updateCurrentLocation(location: Location?) {
        destinationPointerView.currentLocation = location
    }

    private fun updateDestinationLocation(location: Location?) {
        destinationPointerView.destinationLocation = location
    }

    private fun updateLocationState(locationState: LocationState?) {
        if (locationState != null) {
            locationStateView.state = locationState
            locationStateView.visibility = if (locationState == UPDATING) View.INVISIBLE else View.VISIBLE
            locationStateView.isClickable = !locationState.isEnabled
        } else {
            locationStateView.visibility = View.INVISIBLE
            locationStateView.isClickable = false
        }

        destinationPointerView.visibility = if (locationState == UPDATING) View.VISIBLE else View.INVISIBLE
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fineLocationManager.requestUpdates(1000, 1f)
    }

    @SuppressLint("MissingPermission")
    private fun stopLocationUpdates() {
        fineLocationManager.removeUpdates()
    }
}