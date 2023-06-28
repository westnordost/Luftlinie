package de.westnordost.luftlinie.geocoding

data class GeocodingResult(
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
    val importance: Double,
    val key: String,
    val value: String
)
