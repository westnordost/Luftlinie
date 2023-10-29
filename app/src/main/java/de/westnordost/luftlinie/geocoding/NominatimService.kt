package de.westnordost.luftlinie.geocoding

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {
    @GET("search?format=json")
    fun search(
        @Query("q") query: String,
        @Query("accept-language") language: String?,
        @Query("limit") limit: Int?
    ): Call<List<GeocodingResultJson>>
}

@Keep @JsonClass(generateAdapter = true)
data class GeocodingResultJson(
    val display_name: String,
    val lat: String,
    val lon: String,
    val importance: Double,
    val `class`: String,
    val type: String
)