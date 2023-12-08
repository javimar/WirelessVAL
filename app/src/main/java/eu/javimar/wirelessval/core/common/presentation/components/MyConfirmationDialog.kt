package eu.javimar.wirelessval.core.common.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.ui.theme.Negative10
import eu.javimar.wirelessval.ui.theme.Negative50
import eu.javimar.wirelessval.ui.theme.Positive10
import eu.javimar.wirelessval.ui.theme.Positive50

@Composable
fun MyConfimationDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    @StringRes title: Int,
    @StringRes body: Int,
    @StringRes posText: Int,
    @StringRes negText: Int,
    icon: @Composable () -> Unit = {},
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            modifier = modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp)
                ),
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(id = title),
                    textAlign = TextAlign.Center
                )
            },
            icon = icon,
            text = {
                Text(
                    text = stringResource(id = body),
                    textAlign = TextAlign.Center
                )
            },

            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Positive50,
                        contentColor = Positive10
                    )
                ) {
                    Text(
                        text = stringResource(id = posText)
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Negative50,
                        contentColor = Negative10
                    )
                ) {
                    Text(
                        text = stringResource(id = negText)
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun ConfirmationPreview() {
    MyConfimationDialog(
        showDialog = true,
        title = R.string.dialog_refresh_title,
        body = R.string.dialog_refresh_confirmation,
        posText = R.string.dialog_button_ok,
        negText = R.string.dialog_button_cancel,
        icon = {
            ImagePainter(
                painter = R.drawable.ic_location,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        onConfirm = {},
        onDismiss = {}
    )
}