package com.danilovfa.common.uikit.composables.picker.date

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.button.Button
import com.danilovfa.common.uikit.composables.button.OutlinedButton
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material3.DatePickerDialog as Material3DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    date: LocalDate?,
    onDatePicked: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    selectableDates: List<LocalDate>? = null,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds(),
        selectableDates = selectableDates?.let { SelectableDatesImpl(it) } ?: DatePickerDefaults.AllDates
    )

    LaunchedEffect(date) {
        datePickerState.selectedDateMillis =
            date?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    }

    Material3DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                text = stringResource(strings.save),
                enabled = datePickerState.selectedDateMillis != null,
                onClick = {
                    onDismissRequest()
                    datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                        val selectedDate = Instant.fromEpochMilliseconds(selectedDateMillis)
                            .toLocalDateTime(TimeZone.UTC)
                            .date

                        onDatePicked(selectedDate)
                    }
                },
            )
        },
        dismissButton = {
            OutlinedButton(
                text = stringResource(strings.dismiss),
                onClick = onDismissRequest,
            )
        },
        modifier = modifier
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}