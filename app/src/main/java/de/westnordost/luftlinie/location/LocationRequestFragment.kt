package de.westnordost.luftlinie.location

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import de.westnordost.luftlinie.MainViewModel
import de.westnordost.luftlinie.R
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/** Manages the process to ensure that the app can access the user's location. Two steps:
 *
 *  1. ask for permission
 *  2. ask for location to be turned on
 *
 * */
class LocationRequestFragment : Fragment() {

    private val mainModel: MainViewModel by sharedViewModel()

    private var state: LocationState?
    set(value) { mainModel.locationState.value = value }
    get() = mainModel.locationState.value

    private var inProgress = false
    private var locationProviderChangedReceiver: BroadcastReceiver? = null

    /* Lifecycle */

    override fun onStop() {
        super.onStop()
        unregisterForLocationProviderChanges()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (state != null) outState.putString("locationState", state!!.name)
        outState.putBoolean("inProgress", inProgress)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            val stateName = savedInstanceState.getString("locationState")
            if (stateName != null) state = LocationState.valueOf(stateName)
            inProgress = savedInstanceState.getBoolean("inProgress")
        }
    }

    /** Start location request process. When already started, will not be started again.  */
    fun startRequest() {
        if (!inProgress) {
            inProgress = true
            state = null
            nextStep()
        }
    }

    private fun nextStep() {
        if (state == null || state == LocationState.DENIED) {
            requestLocationPermissions()
        } else if (state == LocationState.ALLOWED) {
            requestLocationSettingsToBeOn()
        } else if (state == LocationState.ENABLED) {
            finish()
        }
    }

    private fun finish() {
        inProgress = false
    }

    /* Step 1: Ask for permission */

    private fun requestLocationPermissions() {
        if (requireContext().hasLocationPermission()) {
            state = LocationState.ALLOWED
            nextStep()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // must be for someone else...
        if (requestCode != LOCATION_PERMISSION_REQUEST) return
        if (permissions.firstOrNull() != Manifest.permission.ACCESS_FINE_LOCATION) return
        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions() // retry then...
        } else {
            deniedLocationPermissions()
        }
    }

    private fun deniedLocationPermissions() {
        state = LocationState.DENIED
        finish()
    }

    /* Step 2: Ask for location to be turned on */

    private fun requestLocationSettingsToBeOn() {
        if (requireContext().isLocationOn()) {
            mainModel.locationState.value = LocationState.ENABLED
            nextStep()
        } else {
            val dlg = AlertDialog.Builder(requireContext())
                .setMessage(R.string.turn_on_location_request)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                        LOCATION_TURN_ON_REQUEST
                    )
                }
                .setNegativeButton(android.R.string.no) { _, _ -> cancelTurnLocationOnDialog() }
                .setOnCancelListener { cancelTurnLocationOnDialog() }
                .create()

            // the user may turn on location in the pull-down-overlay, without actually going into
            // settings dialog
            registerForLocationProviderChanges(dlg)
            dlg.show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // must be for someone else...
        if (requestCode != LOCATION_TURN_ON_REQUEST) return
        // we ignore the resultCode, because we always get Activity.RESULT_CANCELED. Instead, we
        // check if the conditions are fulfilled now
        requestLocationSettingsToBeOn()
    }

    private fun registerForLocationProviderChanges(dlg: AlertDialog) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                dlg.dismiss()
                unregisterForLocationProviderChanges()
                requestLocationSettingsToBeOn()
            }
        }
        requireContext().registerReceiver(receiver,
            createLocationAvailabilityIntentFilter()
        )
        locationProviderChangedReceiver = receiver
    }

    private fun unregisterForLocationProviderChanges() {
        locationProviderChangedReceiver?.let { receiver ->
            requireContext().unregisterReceiver(receiver)
        }
        locationProviderChangedReceiver = null
    }

    private fun cancelTurnLocationOnDialog() {
        unregisterForLocationProviderChanges()
        finish()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1
        private const val LOCATION_TURN_ON_REQUEST = 2
    }
}