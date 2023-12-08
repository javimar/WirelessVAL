package eu.javimar.wirelessval.features.wifi.presentation.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.javimar.wirelessval.R
import eu.javimar.wirelessval.core.common.presentation.components.ImagePainter

@Composable
fun MyTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val scrollState = rememberScrollState(0)
    LaunchedEffect(scrollState.maxValue) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .verticalScroll(scrollState),
        shape = RoundedCornerShape(8.dp),
        value = text,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = keyBoardActions,
//        colors = OutlinedTextFieldDefaults.colors(
//            disabledTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
//            focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
//            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
//            disabledBorderColor = MaterialTheme.colorScheme.secondary,
//        ),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF07145A)
@Composable
fun TextFieldPreview() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        MyTextField(
            text = "",
            textStyle = MaterialTheme.typography.labelSmall,
            label = "Hola",
            leadingIcon = {
                ImagePainter(
                    painter = R.drawable.ic_comments,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            trailingIcon = null,
        )
    }
}