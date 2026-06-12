package de.pauljako.mover.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import de.pauljako.mover.R
import de.pauljako.mover.presentation.Place
import de.pauljako.mover.presentation.Transitous
import de.pauljako.mover.presentation.theme.MoverTheme
import de.pauljako.mover.presentation.ui.Screen
import kotlinx.coroutines.launch

@Composable
fun SearchResultView(backStack: SnapshotStateList<Any>, query: String) {
    MoverTheme {
        val scope = rememberCoroutineScope()
        var searchResults by remember { mutableStateOf(emptyList<Place>()) }
        var isLoading by remember { mutableStateOf(true) }

        fun load() {
            scope.launch {
                searchResults = Transitous().searchStop(query)
                isLoading = false
            }
        }
        load()

        if (isLoading) {
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
                ) { contentPadding -> // ScreenScaffold provides default padding; adjust as needed
                    TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                        item {
                            ListHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(transformationSpec),
                            ) {
                                Text(text = stringResource(R.string.search_results, query))
                            }
                        }
                        items(searchResults) { station ->
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec)
                                    .minimumVerticalContentPadding(ButtonDefaults.minimumVerticalListContentPadding),
                                transformation = SurfaceTransformation(transformationSpec),
                                onClick = {
                                    backStack.add(
                                        Screen.DepartureScreen(
                                            station.stopId, station.name
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
}
