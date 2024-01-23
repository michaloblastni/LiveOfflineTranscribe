package tech.almost_senseless.voskle.ui.customComposables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tech.almost_senseless.voskle.FetchState
import tech.almost_senseless.voskle.R
import tech.almost_senseless.voskle.VLTAction
import tech.almost_senseless.voskle.VLTState
import tech.almost_senseless.voskle.data.Languages
import tech.almost_senseless.voskle.data.UserPreferences
import tech.almost_senseless.voskle.vosklib.VoskHub

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePicker(
    settings: UserPreferences,
    state: VLTState,
    voskHub: VoskHub,
    onAction: (VLTAction) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = state.languagePickerExpanded,
        onExpandedChange = {
            onAction(VLTAction.SetLanguagePickerState(!state.languagePickerExpanded))
        },
        modifier = Modifier
            .then(modifier)
    ) {
        TextField(
            enabled = isValidFetchState(state),
            readOnly = true,
            value = settings.language.langName,
            onValueChange = { },
            label = {
                    Text(text = stringResource(id = R.string.select_language))
            },
            modifier = Modifier
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.languagePickerExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
        DropdownMenu(
            expanded = state.languagePickerExpanded,
            onDismissRequest = {
                onAction(VLTAction.SetLanguagePickerState(false))
            }) {
            Languages.values().forEach {
                DropdownMenuItem(text = {
                                        Text(text = it.langName)
                }, onClick = {
                    onAction(VLTAction.SetLanguagePickerState(false))
                    onAction(VLTAction.SetLanguage(it))
                    voskHub.reset()
                    voskHub.setModelPath(it.modelPath)
                    if (voskHub.isModelAvailable()) {
                        voskHub.initModel()
                    } else {
                        onAction(VLTAction.ShowDownloadConfirmation(true))
                    }
                })
            }
        }
    }
}

private fun isValidFetchState(state: VLTState): Boolean {
    return when (state.fetchState) {
        FetchState.NONE -> true
        FetchState.NO_MODEL -> true
        FetchState.READY -> true
        else -> false
    }
}