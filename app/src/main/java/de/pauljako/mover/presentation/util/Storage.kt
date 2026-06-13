package de.pauljako.mover.presentation.util

import android.content.SharedPreferences
import androidx.core.content.edit
import de.pauljako.mover.presentation.Place
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class StoredStation(
    val name: String, val id: String
)

class Storage(val sharedPref: SharedPreferences) {
    fun getCachedStations(): MutableList<StoredStation> {
        return Json.decodeFromString<MutableList<StoredStation>>(
            sharedPref.getString(
                "recent_stations", "[]"
            )!!
        )
    }

    fun addCachedStation(stop: Place) {
        val cachedStations = getCachedStations()
        cachedStations.removeIf { it.id == stop.stopId }
        cachedStations.add(0, StoredStation(stop.name, stop.stopId))
        while (cachedStations.size > 10) {
            cachedStations.removeAt(cachedStations.lastIndex)
        }
        sharedPref.edit {
            putString("recent_stations", Json.encodeToString(cachedStations))
            apply()
        }
    }

}