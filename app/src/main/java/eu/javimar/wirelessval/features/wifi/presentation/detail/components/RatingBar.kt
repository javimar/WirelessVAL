package eu.javimar.wirelessval.features.wifi.presentation.detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    stars: Int = 5,
    starsColor: Color = MaterialTheme.colorScheme.primary,
) {
    val filledStars = floor(rating).toInt()
    val unfilledStars = (stars - ceil(rating)).toInt()
    val halfStar = !(rating.rem(1).equals(0.0))
    Row(
        modifier = modifier
    ) {
        repeat(filledStars) {
            Icon(
                modifier = Modifier.size(56.dp),
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = starsColor
            )
        }
        if (halfStar) {
            Icon(
                modifier = Modifier.size(56.dp),
                imageVector = Icons.Outlined.StarHalf,
                contentDescription = null,
                tint = starsColor
            )
        }
        repeat(unfilledStars) {
            Icon(
                modifier = Modifier.size(56.dp),
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = starsColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingBarPreview() {
    RatingBar(
        rating = 2.5
    )
}