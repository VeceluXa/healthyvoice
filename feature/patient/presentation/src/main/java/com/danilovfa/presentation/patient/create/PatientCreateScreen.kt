package com.danilovfa.presentation.patient.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.date
import com.danilovfa.common.core.domain.time.format
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.button.ButtonLarge
import com.danilovfa.common.uikit.composables.picker.date.DatePickerDialog
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.composables.textfield.ClickableTextFieldLarge
import com.danilovfa.common.uikit.composables.textfield.TextFieldLarge
import com.danilovfa.common.uikit.composables.toolbar.NavigationIcon
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.event.ObserveEvents
import com.danilovfa.common.uikit.modifier.surface
import com.danilovfa.common.uikit.preview.ThemePreviewParameter
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.domain.common.model.Sex
import com.danilovfa.presentation.patient.common.iconPainter
import com.danilovfa.presentation.patient.common.textRes
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.Intent
import com.danilovfa.presentation.patient.create.store.PatientCreateStore.State
import kotlinx.datetime.LocalDate

@Composable
internal fun PatientCreateScreen(component: PatientCreateComponent) {
    val state by component.stateFlow.collectAsState()

    ObserveEvents(component.eventDelegate)
    PatientCreateLayout(
        state = state,
        onIntent = component::onIntent
    )
}

@Composable
private fun PatientCreateLayout(
    state: State,
    onIntent: (Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
    ) {
        Toolbar(
            title = getScreenTitle(state.isEdit),
            navigationIcon = NavigationIcon.Back,
            onNavigationClick = { onIntent(Intent.OnBackClicked) }
        )

        PatientCreateContent(
            state = state,
            onIntent = onIntent,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        ButtonLarge(
            text = getButtonTitle(state.isEdit),
            loading = state.isSaving,
            enabled = state.canSave,
            onClick = { onIntent(Intent.OnSaveClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimension.layoutMainMargin)
        )
    }
}

@Composable
private fun PatientCreateContent(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            TextFieldLarge(
                value = state.name,
                onValueChange = { onIntent(Intent.OnNameChanged(it)) },
                labelText = stringResource(strings.patient_name),
                isRequired = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
            VSpacer(AppDimension.layoutMainMargin)
        }

        item {
            BirthDatePicker(
                birthDate = state.birthDate,
                onBirthDateChanged = { onIntent(Intent.OnBirthDateChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
            VSpacer(AppDimension.layoutMainMargin)
        }

        item {
            SexPicker(
                selectedSex = state.sex,
                onSexChanged = { onIntent(Intent.OnSexChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
            VSpacer(AppDimension.layoutMainMargin)
        }

        item {
            TextFieldLarge(
                value = state.address,
                onValueChange = { onIntent(Intent.OnAddressChanged(it)) },
                labelText = stringResource(strings.patient_address),
                isRequired = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
            VSpacer(AppDimension.layoutMainMargin)
        }

        item {
            TextFieldLarge(
                value = state.note,
                onValueChange = { onIntent(Intent.OnNoteChanged(it)) },
                labelText = stringResource(strings.patient_note),
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
            VSpacer(AppDimension.layoutMainMargin)
        }
    }
}

@Composable
private fun SexPicker(
    selectedSex: Sex,
    onSexChanged: (Sex) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppDimension.layoutMediumMargin),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Sex.entries.forEach { sex ->
            val isSelected = sex == selectedSex

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .height(AppDimension.minTouchSize)
                    .surface(
                        backgroundColor = if (isSelected) AppTheme.colors.primary.copy(alpha = 0.2f) else AppTheme.colors.surface,
                        onClick = { onSexChanged(sex) }
                    )
                    .offset(x = -(AppDimension.iconSize / 2))
            ) {
                Icon(
                    painter = sex.iconPainter,
                    contentDescription = stringResource(sex.textRes),
                    tint = AppTheme.colors.primary
                )

                HSpacer(AppDimension.layoutMediumMargin)

                Text(
                    text = stringResource(sex.textRes),
                    style = AppTypography.titleMedium18,
                    color = AppTheme.colors.textPrimary
                )
            }
        }
    }
}

@Composable
private fun BirthDatePicker(
    birthDate: LocalDate?,
    onBirthDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDialogVisible by remember { mutableStateOf(false) }

    ClickableTextFieldLarge(
        value = birthDate?.format { date() } ?: "",
        onClick = { isDialogVisible = true },
        labelText = stringResource(strings.patient_birth_date),
        isRequired = true,
        modifier = modifier
    )

    if (isDialogVisible) {
        DatePickerDialog(
            onDismissRequest = { isDialogVisible = false },
            date = birthDate,
            onDatePicked = { onBirthDateChanged(it) }
        )
    }
}

@Composable
private fun getButtonTitle(isEdit: Boolean) =
    stringResource(if (isEdit) strings.save else strings.add)

@Composable
private fun getScreenTitle(isEdit: Boolean) =
    stringResource(if (isEdit) strings.patient_edit_title else strings.patient_create_title)

@Composable
@Preview
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) isDark: Boolean) {
    AppTheme(isDark) {
        PatientCreateLayout(
            state = State(patient = null),
            onIntent = {}
        )
    }
}