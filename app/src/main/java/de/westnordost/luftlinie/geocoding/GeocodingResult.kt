package de.westnordost.luftlinie.geocoding

import de.westnordost.luftlinie.location.LngLat

data class GeocodingResult(
    val displayName: String,
    val position: LngLat,
    val importance: Double,
    val key: String,
    val value: String
)
