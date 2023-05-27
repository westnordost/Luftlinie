package de.westnordost.luftlinie.location

import android.net.Uri

fun parseGeoUri(uri: Uri): LngLat? {
    if (uri.scheme != "geo") return null

    val geoUriRegex = Regex("(-?[0-9]*\\.?[0-9]+),(-?[0-9]*\\.?[0-9]+).*")
    val match = geoUriRegex.matchEntire(uri.schemeSpecificPart.removePrefix("//")) ?: return null

    val latitude = match.groupValues[1].toDoubleOrNull() ?: return null
    if (latitude < -90 || latitude > +90) return null
    val longitude = match.groupValues[2].toDoubleOrNull() ?: return null
    if (longitude < -180 || longitude > +180) return null

    return LngLat(longitude, latitude)
}
