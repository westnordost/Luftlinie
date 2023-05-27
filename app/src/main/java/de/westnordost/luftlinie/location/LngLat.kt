package de.westnordost.luftlinie.location

data class LngLat(val longitude: Double, val latitude: Double)

fun parseLatLon(str: String): LngLat? {
    val latLon = str.split(',', limit = 2).map { it.trim() }
    if (latLon.size != 2) return null
    val latitude = latLon[0].toDoubleOrNull() ?: return null
    val longitude = latLon[1].toDoubleOrNull() ?: return null
    return LngLat(longitude, latitude)
}
