package eu.javimar.wirelessval.core.common.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.core.common.toSp
import eu.javimar.wirelessval.ui.theme.colorOpinion0
import eu.javimar.wirelessval.ui.theme.colorOpinion1
import eu.javimar.wirelessval.ui.theme.colorOpinion2
import eu.javimar.wirelessval.ui.theme.colorOpinion3
import eu.javimar.wirelessval.ui.theme.colorOpinion4
import eu.javimar.wirelessval.ui.theme.colorOpinion5

@Composable
fun SetCircle(
    opinion: Double,
    size: Dp,
    font: Dp,
) {
    Box(
        contentAlignment= Alignment.Center,
        modifier = Modifier
            .background(
                color = getOpinionColor(opinion),
                shape = CircleShape
            )
            .size(size)
    ) {
        Text(
            text = opinion.toString(),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = font.toSp(),
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(4.dp),
        )
    }
}

fun getOpinionColor(opinion: Double?): Color {
    if (opinion == null) return colorOpinion0
    return when (opinion) {
        in 0.0..0.9 -> colorOpinion0
        in 1.0..1.9 -> colorOpinion1
        in 2.0..2.9 -> colorOpinion2
        in 3.0..3.9 -> colorOpinion3
        in 4.0..4.9 -> colorOpinion4
        5.0 -> colorOpinion5
        else -> colorOpinion0
    }
}

@Preview
@Composable
fun SectionPreview() {
    SetCircle(
        opinion = 1.5,
        size = 36.dp,
        font = 16.dp,
    )
}
