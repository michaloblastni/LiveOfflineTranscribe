package tech.almost_senseless.voskle.ui.customComposables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import tech.almost_senseless.voskle.VLTAction
import tech.almost_senseless.voskle.VLTState
import tech.almost_senseless.voskle.data.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Textarea(
    settings: UserPreferences,
    state: VLTState,
    modifier: Modifier = Modifier,
    onAction: (VLTAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.maxValue) {
        if (state.isRecording && settings.autoscroll) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    TextField(
        value = state.transcript,
        onValueChange = { onAction(VLTAction.UpdateTranscript(it)) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
            .verticalScroll(scrollState)
            .then(modifier),
        textStyle = LocalTextStyle.current.copy(fontSize = settings.transcriptFontRatio.em)
    )
}