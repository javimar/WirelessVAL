package eu.javimar.wirelessval.core.common.location

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.ui.theme.Negative40

@Composable
fun LocationDialog(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes body: Int,
    @StringRes posText: Int,
    @StringRes negText: Int,
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if(showDialog) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                ),
            ) {
                Column(
                    modifier = modifier.background(
                        color = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter  = ColorFilter.tint(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .padding(top = 35.dp)
                            .height(70.dp)
                            .fillMaxWidth(),
                    )

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = title),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(id = body),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .background(
                                MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text(
                                text = stringResource(id = negText),
                                fontWeight = FontWeight.Bold,
                                color = Negative40,
                                modifier = Modifier
                                    .padding(top = 5.dp, bottom = 5.dp),
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Start
                            )
                        }
                        TextButton(
                            onClick = onConfirm
                        ) {
                            Text(
                                text = stringResource(id = posText),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .padding(top = 5.dp, bottom = 5.dp),
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name="My Custom Dialog")
@Composable
fun MyDialogUIPreview(){
    LocationDialog(
        icon = R.drawable.ic_location,
        title = R.string.dialog_access_location_title,
        body = R.string.permission_gps,
        posText = R.string.dialog_access_location,
        negText = R.string.dialog_button_cancel,
        showDialog = true,
        onConfirm = {},
        onDismiss = {}
    )
}