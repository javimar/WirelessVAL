package eu.javimar.wirelessval.features.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.presentation.components.MyAppBar
import eu.javimar.wirelessval.core.common.presentation.components.SegmentedControl
import eu.javimar.wirelessval.features.settings.presentation.components.SettingsSwitch
import eu.javimar.wirelessval.features.settings.presentation.state.WifiSettingsEvent
import eu.javimar.wirelessval.features.settings.presentation.state.WifiSettingsState
import eu.javimar.wirelessval.features.wifi.domain.utils.WifiOrderOptions

@Composable
fun WifisSettingsScreen(
    state: WifiSettingsState,
    onEvent: (WifiSettingsEvent) -> Unit
) {
    Scaffold(
        topBar = {
            MyAppBar(
                title = stringResource(id = R.string.title_settings),
                onNavClick = {
                    onEvent(WifiSettingsEvent.OnBackClick)
                },
                hasRightIcon = false,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        content = {
            Column(
                Modifier
                    .padding(it)
                    .padding(16.dp),
            ) {

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = stringResource(id = R.string.settings_sort_criteria),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                SettingsSwitch(
                    text = R.string.settings_sort_by_name_label,
                    checked = state.orderOptions == WifiOrderOptions.NAME,
                    onChecked = {
                        onEvent(WifiSettingsEvent.SetOrderOption(WifiOrderOptions.NAME))
                    }
                )
                SettingsSwitch(
                    text = R.string.settings_sort_by_distance_label,
                    checked = state.orderOptions == WifiOrderOptions.DISTANCE,
                    onChecked = {
                        onEvent(WifiSettingsEvent.SetOrderOption(WifiOrderOptions.DISTANCE))
                    }
                )
                SettingsSwitch(
                    text = R.string.settings_sort_by_opinion_label,
                    checked = state.orderOptions == WifiOrderOptions.OPINION,
                    onChecked = {
                        onEvent(WifiSettingsEvent.SetOrderOption(WifiOrderOptions.OPINION))
                    }
                )
                Text(
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    text = stringResource(id = R.string.pref_app_settings_label),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if(state.dynamicColorsEnabled) {
                    SegmentedControl(
                        isFirstSelected = state.isAppColorsSelected,
                        items = listOf(
                            stringResource(id = R.string.pref_app_settings_scheme_appcolors),
                            stringResource(id = R.string.pref_app_settings_color_dynamic),
                        ),
                        captions = listOf(
                            stringResource(id = R.string.pref_app_settings_scheme_caption2),
                            stringResource(id = R.string.pref_app_settings_scheme_caption1),
                        ),
                        textStyle = MaterialTheme.typography.bodySmall,
                        activeColor = MaterialTheme.colorScheme.primary,
                        inactiveColor = MaterialTheme.colorScheme.onPrimary,
                        showCaption = true,
                        onTabSelected = { value ->
                            onEvent(WifiSettingsEvent.ToggleDynamic(value))
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                SegmentedControl(
                    isFirstSelected = state.isDarkSelected,
                    items = listOf(
                        stringResource(id = R.string.pref_app_settings_scheme_light),
                        stringResource(id = R.string.pref_app_settings_scheme_dark),
                    ),
                    textStyle = MaterialTheme.typography.bodySmall,
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = MaterialTheme.colorScheme.onPrimary,
                    onTabSelected = { value ->
                        onEvent(WifiSettingsEvent.ToggleDark(value))
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WifisSettingsPreview() {
    WifisSettingsScreen(
        state = WifiSettingsState(
            orderOptions = WifiOrderOptions.DISTANCE
        ),
        onEvent = {}
    )
}