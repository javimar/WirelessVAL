package eu.javimar.wirelessval.core.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import eu.javimar.wirelessval.core.common.toBoolean
import eu.javimar.wirelessval.core.common.toInt

@Composable
fun SegmentedControl(
    modifier: Modifier = Modifier,
    isFirstSelected: Boolean,
    onTabSelected: (Boolean) -> Unit,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onPrimary,
    items: List<String> = listOf("Male", "Female"),
    captions: List<String> = listOf("Male", "Female"),
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    showCaption: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = activeColor,
                    shape = RoundedCornerShape(32.dp)
                )
                .wrapContentHeight(),
            selectedTabIndex = isFirstSelected.toInt(),
            contentColor = Color.Transparent,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[isFirstSelected.toInt()])
                        .fillMaxSize()
                        .clip(RoundedCornerShape(32.dp))
                        .background(activeColor)
                        .zIndex(-1F)
                )
            }) {
            items.forEachIndexed { index, item ->
                Tab(
                    selected = isFirstSelected.toInt() == index,
                    onClick = {
                        onTabSelected(index.toBoolean())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(
                            horizontal = 8.dp, vertical = 8.dp
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    onTabSelected(index.toBoolean())
                                })
                            .fillMaxWidth(),
                        text = item,
                        textAlign = TextAlign.Center,
                        color = if (isFirstSelected == index.toBoolean()) {
                            inactiveColor
                        } else {
                            activeColor
                        },
                        fontWeight = FontWeight.SemiBold,
                        style = textStyle,
                    )
                }
            }
        }
        if(showCaption) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = captions[0],
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = captions[1],
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SegmentedControlPreview() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        color = Color.Transparent
    ) {
        SegmentedControl(
            isFirstSelected = false,
            onTabSelected = {},
            showCaption = true
        )
    }
}