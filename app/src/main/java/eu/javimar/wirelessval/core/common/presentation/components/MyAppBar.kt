package eu.javimar.wirelessval.core.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String,
    onNavClick: () -> Unit = {},
    showNavIcon: Boolean = true,
    hasRightIcon: Boolean = false,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    @DrawableRes rightIcon: Int = R.drawable.ic_sort,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    containerColor: Color = MaterialTheme.colorScheme.surface,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = style,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
        },
        navigationIcon = {
            if(showNavIcon) {
                IconButton(onClick = onNavClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_nav),
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
            }
        },
        actions = {
            if(hasRightIcon) {
                ImagePainter(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(24.dp)
                        .clickable {
                            onNavClick()
                        },
                    painter = rightIcon,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor
        )
    )
}

@Preview(showBackground = true)
@Composable
fun MyAppBarPreview() {
    MyAppBar(
        hasRightIcon = true,
        rightIcon = R.drawable.ic_sort,
        title = "My App Bar Title",
        onNavClick = {}
    )
}