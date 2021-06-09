package de.westnordost.luftlinie.geocoding

import de.westnordost.luftlinie.BuildConfig
import de.westnordost.luftlinie.R
import de.westnordost.osmfeatures.AndroidFeatureDictionary
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val geocodingModule = module {

    viewModel { GeocodingViewModel(get()) }
    single { AndroidFeatureDictionary.create(androidContext().assets, "osmfeatures") }

    single { OkHttpClient.Builder().addInterceptor { chain ->
        chain.proceed(chain.request().newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", androidContext().getString(R.string.app_name) + " "+ BuildConfig.VERSION_NAME)
                .build())
        }.build()
    }

    single { NominatimGeocoder(
        HttpUrl.parse("https://nominatim.openstreetmap.org/")!!,
        get()
    ) }
}