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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ListHeaderDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import de.pauljako.mover.R
import de.pauljako.mover.presentation.theme.MoverTheme
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

sealed interface Screen : NavKey {
    data object HomeScreen : Screen
    data class DepartureScreen(val stationId: String) : Screen
}

val stations = mapOf<String, String>(
    "Stuttgart Hbf" to "at-Railway-Current-Reference-Data-2026_de:08111:6115:1:1",
    "Schwäbisch Gmünd" to "de-DELFI_de:08136:3077:1:15"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoverApp()
        }
    }
}

@Composable
fun LineBadge(trip: Trip) {
    val line = trip.line
    val backgroundColor = Color("#${trip.lineColor}".toColorInt())
    Row {
        Icon(
            painterResource(trip.tripType.icon),
            contentDescription = trip.tripType.name
        )

        Box(
            modifier = Modifier
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
}

@Composable
fun MoverApp() {
    val backStack = remember { mutableStateListOf<Any>(Screen.HomeScreen) }

    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(rememberSwipeDismissableSceneStrategy()),
        entryProvider = entryProvider {
            entry<Screen.HomeScreen> {
                HomeView(backStack)
            }

            entry<Screen.DepartureScreen> { key ->
                DepartureListView(key.stationId)
            }

        }
    )
}

@Composable
fun HomeView(backStack: SnapshotStateList<Any>) {
    MoverTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            ScreenScaffold(
                scrollState = listState,
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
                            Text(text = stringResource(R.string.app_name))
                        }
                    }
                    items(stations.keys.toList()) { stationName ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                            transformation = SurfaceTransformation(transformationSpec),
                            onClick = {
                                stations[stationName]?.let {
                                    backStack.add(
                                        Screen.DepartureScreen(
                                            it
                                        )
                                    )
                                }
                            },
                        ) {
                            Text(
                                text = stationName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
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
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                LineBadge(trip)
                                Text(
                                    trip.destination.name,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                trip.place.departure?.let {
                                    val duration = Duration.between(
                                        Instant.now(),
                                        Instant.parse(it)
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