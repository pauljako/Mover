package de.pauljako.mover.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation3.rememberSwipeDismissableSceneStrategy
import de.pauljako.mover.presentation.Trip
import de.pauljako.mover.presentation.ui.screens.DepartureListView
import de.pauljako.mover.presentation.ui.screens.HomeView
import de.pauljako.mover.presentation.ui.screens.SearchResultView
import de.pauljako.mover.presentation.util.Storage

sealed interface Screen : NavKey {
    data object HomeScreen : Screen
    data class DepartureScreen(val stationId: String, val name: String) : Screen
    data class SearchResultScreen(val query: String) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = this.getPreferences(MODE_PRIVATE)
        val storage = Storage(sharedPref)
        setContent {
            MoverApp(storage)
        }
    }
}

@Composable
fun LineBadge(trip: Trip) {
    val line = trip.line
    val backgroundColor = Color("#${trip.lineColor}".toColorInt())
    Row {
        Icon(
            painterResource(trip.tripType.icon), contentDescription = trip.tripType.name
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(
                    horizontal = 8.dp, vertical = 2.dp
                )
        ) {
            Text(
                text = line, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MoverApp(storage: Storage) {
    val backStack = remember { mutableStateListOf<Any>(Screen.HomeScreen) }

    NavDisplay(
        backStack = backStack,
        sceneStrategies = listOf(rememberSwipeDismissableSceneStrategy()),
        entryProvider = entryProvider {
            entry<Screen.HomeScreen> {
                HomeView(storage, backStack)
            }

            entry<Screen.DepartureScreen> { key ->
                DepartureListView(storage, key.stationId, key.name)
            }

            entry<Screen.SearchResultScreen> { key ->
                SearchResultView(backStack, key.query)
            }

        })
}

