package de.westnordost.luftlinie

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import de.westnordost.luftlinie.geocoding.GeocodingFragment
import de.westnordost.luftlinie.location.DestinationFragment
import de.westnordost.luftlinie.location.parseGeoUri
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, GeocodingFragment())
                .commit()
        }

        mainModel.destinationLocation.observe(this, this::onNewDestination)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        handleGeoUri()
    }

    private fun handleGeoUri() {
        if (intent.action != Intent.ACTION_VIEW) return
        val data = intent.data ?: return
        if ("geo" != data.scheme) return
        val geo = parseGeoUri(data) ?: return
        mainModel.destinationLocation.value = Location(null as String?).apply {
            longitude = geo.longitude
            latitude = geo.latitude
        }
    }

    private fun onNewDestination(location: Location?) {
        if (location != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fragment_open_enter, R.anim.fragment_open_exit,
                    R.anim.fragment_close_enter, R.anim.fragment_close_exit
                )
                .replace(R.id.fragmentContainer, DestinationFragment())
                .addToBackStack(null)
                .commit()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return
        }
        super.onBackPressed()
    }
}