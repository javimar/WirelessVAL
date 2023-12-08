package eu.javimar.wirelessval.core.common.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R

@Composable
fun MyCustomSnackBar(
    message: String,
    onActionClicked: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    textColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Snackbar(
        containerColor = containerColor,
        dismissAction = {
            ImagePainter(
                modifier = Modifier
                    .clickable {
                        onActionClicked()
                    }
                    .padding(end = 12.dp)
                    .width(24.dp),
                painter = R.drawable.ic_ok,
                color = MaterialTheme.colorScheme.surface,
            )
        },
        content = {
            Row {
                Text(
                    text = message,
                    color = textColor,
                    fontWeight = FontWeight.Black
                )
            }
        }
    )
}

@Preview
@Composable
fun SnackPreview() {
    MyCustomSnackBar(
        message = "This is a message",
        onActionClicked = {}
    )
}