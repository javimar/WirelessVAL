package eu.javimar.wirelessval.features.about.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import eu.javimar.wirelessval.BuildConfig
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.presentation.components.MyAppBar
import eu.javimar.wirelessval.features.main.presentation.BottomBar

@Composable
fun WifiAboutScreen(
    navController: NavHostController,
    dateState: String
) {
    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
        topBar = {
            MyAppBar(
                title = stringResource(id = R.string.title_about_activity),
                showNavIcon = false,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        content = { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Box {
                        Image(
                            painterResource(id = R.drawable.valencia),
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = ""
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.about_data_version,
                                    BuildConfig.VERSION_NAME
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                textAlign = TextAlign.Start,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.about_copy),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        Image(
                            painterResource(id = R.drawable.applogo),
                            modifier = Modifier
                                .height(96.dp)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = ""
                        )
                        Text(
                            text = stringResource(id = R.string.about_data_from),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(id = R.string.about_data_updated, dateState),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = stringResource(id = R.string.about_instructions),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                    )
                }
                Spacer(modifier = Modifier.padding(24.dp))
            }
        })
}

@Preview(wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE, showBackground = true)
@Composable
fun AboutPreview() {
    WifiAboutScreen(
        navController = rememberNavController(),
        dateState = "30 diciembre 1969"
    )
}
