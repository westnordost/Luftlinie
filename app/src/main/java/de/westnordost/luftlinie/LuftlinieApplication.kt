package de.westnordost.luftlinie

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import de.westnordost.luftlinie.geocoding.geocodingModule
import de.westnordost.luftlinie.location.locationModule
import de.westnordost.osmfeatures.FeatureDictionary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LuftlinieApplication : Application(), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val featureDictionary: FeatureDictionary by inject()

    override fun onCreate(){
        super.onCreate()
        startKoin {
            androidContext(this@LuftlinieApplication)
            modules(geocodingModule, locationModule, mainModule)
        }

        // initialize certain modules beforehand in the background
        launch {
            featureDictionary
        }

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
    }
}