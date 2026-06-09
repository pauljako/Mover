/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package de.pauljako.mover.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import de.pauljako.mover.R
import de.pauljako.mover.presentation.theme.MoverTheme
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DepartureListView("de-DELFI_de:08136:3077:1:15")
        }
    }
}

@Composable
fun LineBadge(
    line: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(
                horizontal = 8.dp,
                vertical = 2.dp
            )
    ) {
        Text(
            text = line,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DepartureListView(stationId: String) {
    MoverTheme {
        val scope = rememberCoroutineScope()
        var stationName by remember { mutableStateOf(stationId) }
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
                        colors =
                            ButtonDefaults.buttonColors(
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
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = stringResource(R.string.departures, stationName))
                        }
                    }
                    items(trips) { trip ->
                        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.trip, trip.line, trip.destination.name))
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row {
                                Icon(
                                    painterResource(trip.tripType.icon),
                                    contentDescription = trip.tripType.name
                                )
                                LineBadge(trip.line, Color("#${trip.lineColor}".toColorInt()))
                            }
                            Text(
                                trip.destination.name,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            trip.place.departure?.let {
                                Text(
                                    Duration.between(
                                        Instant.now(),
                                        Instant.parse(it)
                                    ).toMinutes().toString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}