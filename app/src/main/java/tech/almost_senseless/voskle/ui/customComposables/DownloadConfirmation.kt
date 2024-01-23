package tech.almost_senseless.voskle.ui.customComposables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import tech.almost_senseless.voskle.R
import tech.almost_senseless.voskle.VLTAction
import tech.almost_senseless.voskle.VLTViewModel
import tech.almost_senseless.voskle.data.UserPreferences

@Composable
fun DownloadConfirmation(
    settings: UserPreferences,
    downloadFunction: (VLTViewModel, String) -> Unit,
    onAction: (VLTAction) -> Unit
) {
    AlertDialog(onDismissRequest = {
        onAction(
            VLTAction.ShowDownloadConfirmation(false)
        )
    },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(text = stringResource(id = R.string.dialog_confirm),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAction(VLTAction.ShowDownloadConfirmation(false))
                            onAction(VLTAction.DownloadModel(downloadFunction, settings.language.modelPath))
                        }
                        .padding(16.dp)
                )
            }
        },
        dismissButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(text = stringResource(id = R.string.dialog_cancel),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAction(VLTAction.ShowDownloadConfirmation(false))
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = stringResource(id = R.string.download_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.download_dialog_text, settings.language.langName))
        }
    )
}