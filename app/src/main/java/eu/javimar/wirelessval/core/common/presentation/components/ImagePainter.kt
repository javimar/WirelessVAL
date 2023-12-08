package eu.javimar.wirelessval.core.common.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ImagePainter(
    modifier: Modifier = Modifier,
    @DrawableRes painter: Int,
    color: Color? = null,
) {
    Image(
        painter = painterResource(id = painter),
        modifier = modifier
            .width(48.dp),
        colorFilter = color?.let {ColorFilter.tint(color) },
        contentDescription = null
    )
}