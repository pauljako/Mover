package de.pauljako.mover.presentation.ui.screens

import android.app.RemoteInput
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.FilledTonalButton
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import de.pauljako.mover.R
import de.pauljako.mover.presentation.theme.MoverTheme
import de.pauljako.mover.presentation.ui.Screen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class StoredStation(
    val name: String, val id: String
)

@Composable
fun HomeView(sharedPref: SharedPreferences, backStack: SnapshotStateList<Any>) {
    MoverTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    it.data?.let { data ->
                        val results: Bundle = RemoteInput.getResultsFromIntent(data)
                        val result = results.getCharSequence("stop_search") as String
                        backStack.add(Screen.SearchResultScreen(result))
                    }
                }
            val cachedStations = Json.decodeFromString<MutableList<StoredStation>>(
                sharedPref.getString(
                    "recent_stations", "[]"
                )!!
            )
            ScreenScaffold(
                scrollState = listState,
            ) { contentPadding -> // ScreenScaffold provides default padding; adjust as needed
                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                    item {
                        ListHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = stringResource(R.string.app_name))
                        }
                    }
                    item {
                        val searchLabel = stringResource(R.string.stop_search)
                        FilledTonalButton(onClick = {
                            val intent: Intent =
                                RemoteInputIntentHelper.createActionRemoteInputIntent()
                            val remoteInputs: List<RemoteInput> = listOf(
                                RemoteInput.Builder("stop_search").setLabel(searchLabel)
                                    .wearableExtender {
                                        setEmojisAllowed(false)
                                        setInputActionType(EditorInfo.IME_ACTION_SEARCH)
                                    }.build()
                            )

                            RemoteInputIntentHelper.putRemoteInputsExtra(
                                intent, remoteInputs
                            )

                            launcher.launch(intent)
                        }, transformation = SurfaceTransformation(transformationSpec)) {
                            Icon(
                                painterResource(R.drawable.search_24px),
                                contentDescription = "Search Icon"
                            )
                        }
                    }
                    items(cachedStations) { station ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                            transformation = SurfaceTransformation(transformationSpec),
                            onClick = {
                                backStack.add(
                                    Screen.DepartureScreen(
                                        station.id, station.name
                                    )
                                )
                            },
                        ) {
                            Text(
                                text = station.name,
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
