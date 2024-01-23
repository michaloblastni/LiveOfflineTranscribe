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

abstract class ErrorDialog(
    private val dismissAction: VLTAction = VLTAction.DismissError,
    protected val okAction: VLTAction = VLTAction.DismissError,
    open val contactUsCallback: () -> Unit,
    open val onAction: (VLTAction) -> Unit
) {
    private fun onDismissRequest() {
        onAction(dismissAction)
    }
    @Composable
    protected open fun ConfirmButton() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider()
            Text(text = stringResource(id = R.string.dialog_ok),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(okAction) }
                    .padding(16.dp)
            )
            Divider()
            Text(text = stringResource(id = R.string.contact_us),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { contactUsCallback() }
                    .padding(16.dp)
                )
        }
    }
    @Composable
    protected fun DismissButton() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider()
            Text(text = stringResource(id = R.string.dialog_close),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAction(dismissAction) }
                    .padding(16.dp))
        }
    }
    @Composable
    protected abstract fun Title()
    @Composable
    protected abstract fun DialogText()
    @Composable
    fun RenderErrorDialog() {
        AlertDialog(onDismissRequest = { onDismissRequest() },
            confirmButton = { ConfirmButton() },
            dismissButton = { DismissButton() },
            title = { Title() },
            text = { DialogText() })
    }
}

class ConnectionErrorDialog(
    private val modelPath: String,
    private val downloadFunction: (VLTViewModel, String) -> Unit,
    val message: String = "",
    override val contactUsCallback: () -> Unit,
    override val onAction: (VLTAction) -> Unit
): ErrorDialog(contactUsCallback = contactUsCallback, onAction = onAction) {
    @Composable
    override fun ConfirmButton() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider()
            Text(text = stringResource(id = R.string.try_again),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAction(super.okAction)
                        onAction(VLTAction.DownloadModel(downloadFunction, modelPath))
                    }
                    .padding(16.dp)
            )
            Divider()
            Text(text = stringResource(id = R.string.contact_us),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { contactUsCallback() }
                    .padding(16.dp)
            )
        }
    }

    @Composable
    override fun Title() {
        Text(text = stringResource(id = R.string.connection_failed_title))
    }

    @Composable
    override fun DialogText() {
        if (message.isNotEmpty()) {
            Text(text = stringResource(id = R.string.connection_failed_with_message, message))
        } else {
            Text(text = stringResource(id = R.string.connection_failed))
        }
    }
}

class DataProcessingErrorDialog(
    private val modelPath: String,
    private val downloadFunction: (VLTViewModel, String) -> Unit,
    private val message: String,
    override val contactUsCallback: () -> Unit,
    override val onAction: (VLTAction) -> Unit
): ErrorDialog(contactUsCallback = contactUsCallback, onAction = onAction) {
    @Composable
    override fun ConfirmButton() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider()
            Text(text = stringResource(id = R.string.try_again),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAction(super.okAction)
                        onAction(VLTAction.DownloadModel(downloadFunction, modelPath))
                    }
                    .padding(16.dp)
            )
            Divider()
            Text(text = stringResource(id = R.string.contact_us),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { contactUsCallback() }
                    .padding(16.dp)
            )
        }
    }

    @Composable
    override fun Title() {
        Text(text = stringResource(id = R.string.unexpected_download_response_title))
    }

    @Composable
    override fun DialogText() {
        Text(text = stringResource(id = R.string.unexpected_download_response, message))
    }
}

class UnexpectedResponseDialog(
    private val modelPath: String,
    private val downloadFunction: (VLTViewModel, String) -> Unit,
    val message: String,
    override val contactUsCallback: () -> Unit,
    override val onAction: (VLTAction) -> Unit
): ErrorDialog(contactUsCallback = contactUsCallback, onAction = onAction) {
    @Composable
    override fun ConfirmButton() {
        Column(modifier = Modifier.fillMaxWidth()) {
            Divider()
            Text(text = stringResource(id = R.string.try_again),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onAction(super.okAction)
                        onAction(VLTAction.DownloadModel(downloadFunction, modelPath))
                    }
                    .padding(16.dp)
            )
            Divider()
            Text(text = stringResource(id = R.string.contact_us),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { contactUsCallback() }
                    .padding(16.dp)
            )
        }
    }

    @Composable
    override fun Title() {
        Text(text = stringResource(id = R.string.data_processing_failed_title))
    }

    @Composable
    override fun DialogText() {
        Text(text = stringResource(id = R.string.data_processing_failed, message))
    }
}

class TimeouteErrorDialog(
    override val contactUsCallback: () -> Unit,
    override val onAction: (VLTAction) -> Unit
): ErrorDialog(contactUsCallback = contactUsCallback, onAction = onAction) {
    @Composable
    override fun Title() {
        Text(text = stringResource(id = R.string.timeout_title))
    }

    @Composable
    override fun DialogText() {
        Text(text = stringResource(id = R.string.timeout))
    }
}

class ModelErrorDialog(
    private val message: String,
    override val contactUsCallback: () -> Unit,
    override val onAction: (VLTAction) -> Unit
): ErrorDialog(contactUsCallback = contactUsCallback, onAction = onAction) {
    @Composable
    override fun Title() {
        Text(text = stringResource(id = R.string.model_error_title))
    }

    @Composable
    override fun DialogText() {
        Text(text = stringResource(id = R.string.model_error, message))
    }
}