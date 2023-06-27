package de.westnordost.luftlinie.location

import android.location.Location
import android.net.Uri

fun parseGeoUri(uri: Uri): Location? {
    if (uri.scheme != "geo") return null
    return parseLocation(uri.schemeSpecificPart.removePrefix("//"))
}

fun parseLocation(string: String): Location? {
    val geoUriRegex = Regex("(-?[0-9]*\\.?[0-9]+),(-?[0-9]*\\.?[0-9]+)(?:;(.*))?.*")
    val match = geoUriRegex.matchEntire(string) ?: return null

    val latitude = match.groupValues[1].toDoubleOrNull() ?: return null
    if (latitude < -90 || latitude > +90) return null
    val longitude = match.groupValues[2].toDoubleOrNull() ?: return null
    if (longitude < -180 || longitude > +180) return null

    val parameters = match.groupValues.getOrNull(3)
        ?.split(';')
        ?.mapNotNull {
            val kv = it.split('=', limit = 2)
            if (kv.size != 2) null else kv[0].lowercase() to kv[1]
        }
        ?.associate { it }
        .orEmpty()

    val accuracy = parameters["u"]?.toFloatOrNull()

    return Location(null as String?).also {
        it.longitude = longitude
        it.latitude = latitude
        if (accuracy != null) it.accuracy = accuracy
    }
}