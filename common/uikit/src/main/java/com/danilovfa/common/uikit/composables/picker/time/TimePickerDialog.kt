package com.danilovfa.common.uikit.composables.picker.time

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.button.Button
import com.danilovfa.common.uikit.composables.button.OutlinedButton
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.modifier.surfaceSection
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    isDialogVisible: MutableState<Boolean>,
    time: Int,
    onTimeChanged: (Int) -> Unit,
    title: String = stringResource(strings.choose_time),
    maxHours: Int = 24
) {
    val timePickerState = rememberTimePickerState(
        initialHour = time / 60,
        initialMinute = time % 60
    )

    LaunchedEffect(time) {
        timePickerState.hour = time / 60
        timePickerState.minute = time % 60
    }

    if (isDialogVisible.value) {
        Dialog(
            onDismissRequest = { isDialogVisible.value = false }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .surfaceSection()
                    .padding(AppDimension.layoutMainMargin)
            ) {
                Text(
                    text = title,
                    style = AppTypography.bodyMedium16,
                    color = AppTheme.colors.textPrimary
                )
                VSpacer(AppDimension.layoutMainMargin)
                TimeInput(
                    state = timePickerState,
                    colors = AppTimePickerDefaults.primary,
                    maxHours = maxHours - 1
                )

                VSpacer(AppDimension.layoutMainMargin)

                Row {
                    OutlinedButton(
                        text = stringResource(strings.dismiss),
                        onClick = { isDialogVisible.value = false },
                        modifier = Modifier.weight(1f)
                    )

                    HSpacer(AppDimension.layoutMediumMargin)

                    Button(
                        text = stringResource(strings.save),
                        onClick = {
                            onTimeChanged(timePickerState.hour * 60 + timePickerState.minute)
                            isDialogVisible.value = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

    }
}