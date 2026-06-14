package de.pauljako.mover.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CircularProgressIndicator
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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DetailedJourneyView(tripId: String) {
    MoverTheme {
        val scope = rememberCoroutineScope()
        var legs by remember { mutableStateOf(emptyList<Trip>()) }
        var isRefreshing by remember { mutableStateOf(true) }

        fun refresh() {
            scope.launch {
                val itinerary = Transitous().getTrip(tripId)
                legs = itinerary.legs
                isRefreshing = false
            }
        }

        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AppScaffold {
                val listState = rememberTransformingLazyColumnState()
                val transformationSpec = rememberTransformationSpec()
                ScreenScaffold(
                    scrollState = listState,
                    edgeButton = {
                        EdgeButton(
                            onClick = {
                                isRefreshing = true
                                refresh()
                            },
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
                                Text(text = stringResource(R.string.journey))
                            }
                        }
                        items(legs) { leg ->
                            val allStops = mutableListOf(leg.start)
                            allStops.addAll(leg.intermediateStops)
                            allStops.add(leg.destination)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            ) {
                                Column {
                                    for (stop in allStops) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 6.dp)
                                        ) {
                                            Text(
                                                stop.name,
                                                modifier = Modifier.weight(1f),
                                                overflow = TextOverflow.MiddleEllipsis,
                                                maxLines = 2
                                            )
                                            val departureTime =
                                                OffsetDateTime.parse(stop.departure ?: stop.arrival)
                                                    .format(
                                                        DateTimeFormatter.ofPattern("HH:mm")
                                                            .withZone(
                                                                ZoneId.systemDefault()
                                                            )
                                                    )
                                            val color =
                                                (if (Instant.parse(stop.departure ?: stop.arrival)
                                                        .isBefore(Instant.now())
                                                ) Color.Gray else Color.White)

                                            Spacer(Modifier.width(4.dp))

                                            Text(
                                                departureTime,
                                                textAlign = TextAlign.Right,
                                                maxLines = 1,
                                                color = color
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        refresh()
    }
}
