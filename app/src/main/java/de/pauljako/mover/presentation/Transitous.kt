package de.pauljako.mover.presentation

import de.pauljako.mover.R
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import work.socialhub.khttpclient.HttpRequest

@Serializable
data class Place @OptIn(ExperimentalSerializationApi::class) constructor(
    val name: String, @JsonNames("id") val stopId: String, val departure: String? = null
)

enum class VehicleType(
    val displayName: String, val icon: Int
) {
    REGIONAL_RAIL(
        "Regional Train", R.drawable.directions_railway_24px
    ),
    HIGHSPEED_RAIL(
        "Long-Distance Train", R.drawable.directions_railway_24px
    ),
    LONG_DISTANCE("Long-Distance Train", R.drawable.directions_railway_24px), TRAM(
        "Tram", R.drawable.directions_railway_24px
    ),
    BUS("Bus", R.drawable.directions_bus_24px), COACH(
        "Coach", R.drawable.directions_bus_24px
    ),
    SUBURBAN("S-Bahn", R.drawable.directions_subway_24px), SUBWAY(
        "Subway", R.drawable.directions_subway_24px
    )
}

@Serializable
data class Trip(
    val place: Place,
    @SerialName("mode") val tripType: VehicleType,
    @SerialName("tripTo") val destination: Place,
    @SerialName("displayName") val line: String,
    @SerialName("routeColor") val lineColor: String = "000000"
)

@Serializable
data class DepartureList(
    @SerialName("stopTimes") val trips: List<Trip>, val place: Place
)

class Transitous {

    val transitousEndpoint = "https://api.transitous.org"

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getDepartures(stopId: String, amount: Int): DepartureList {
        val response =
            HttpRequest().url("$transitousEndpoint/api/v6/stoptimes").query("stopId", stopId)
                .query("n", amount.toString()).get()

        val result = json.decodeFromString<DepartureList>(response.stringBody)
        return result
    }

    suspend fun searchStop(searchText: String): List<Place> {
        val response = HttpRequest().url("$transitousEndpoint/api/v1/geocode").query("type", "STOP")
            .query("text", searchText).get()

        val result = json.decodeFromString<List<Place>>(response.stringBody)
        return result
    }

    suspend fun searchLocationStop(latitude: String, longitude: String): List<Place> {
        val response =
            HttpRequest().url("$transitousEndpoint/api/v1/reverse-geocode").query("type", "STOP")
                .query("place", "$latitude,$longitude").get()

        val result = json.decodeFromString<List<Place>>(response.stringBody)
        return result
    }
}