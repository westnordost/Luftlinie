package de.westnordost.luftlinie.geocoding

import de.westnordost.luftlinie.location.LngLat
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.*

class NominatimGeocoder(
    baseUrl: HttpUrl,
    client: OkHttpClient
) {
    private val service: NominatimService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        service = retrofit.create()
    }

    suspend fun search(
        query: String,
        locale: Locale? = Locale.getDefault(),
        limit: Int? = null
    ): List<GeocodingResult> {
        return service.search(query, locale?.language, limit).await().map {
            GeocodingResult(
                it.display_name,
                LngLat(it.lon.toDouble(), it.lat.toDouble()),
                it.importance,
                it.`class`,
                it.type
            )
        }
    }
}

