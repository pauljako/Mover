package de.pauljako.mover.presentation.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import de.pauljako.mover.R
import de.pauljako.mover.presentation.Transitous
import de.pauljako.mover.presentation.Trip
import de.pauljako.mover.presentation.theme.MoverTheme
import de.pauljako.mover.presentation.ui.LineBadge
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

@Composable
fun DepartureListView(stationId: String, stationName: String) {
    MoverTheme {
        val scope = rememberCoroutineScope()
        var stationName by remember { mutableStateOf(stationName) }
        var trips by remember { mutableStateOf(emptyList<Trip>()) }

        fun refresh() {
            scope.launch {
                val departures = Transitous().getDepartures(stationId, 5)
                stationName = departures.place.name
                trips = departures.trips
            }
        }

        refresh()

        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            ScreenScaffold(
                scrollState = listState,
                edgeButton = {
                    EdgeButton(
                        onClick = { refresh() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    ) {
                        Text("Reload")
                    }
                },
            ) { contentPadding -> // ScreenScaffold provides default padding; adjust as needed
                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                    item {
                        ListHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .minimumVerticalContentPadding(ListHeaderDefaults.minimumTopListContentPadding),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = stringResource(R.string.departures, stationName))
                        }
                    }
                    items(trips) { trip ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LineBadge(trip)
                                Text(
                                    trip.destination.name,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                trip.place.departure?.let {
                                    val duration = Duration.between(
                                        Instant.now(), Instant.parse(it)
                                    ).toMinutes()
                                    Text(if (duration == 0L) "Now" else "In ${duration}min")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
