package com.danilovfa.presentation.patient.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.dateTime
import com.danilovfa.common.core.domain.time.format
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.button.ButtonLarge
import com.danilovfa.common.uikit.composables.dialog.AlertDialog
import com.danilovfa.common.uikit.composables.popup.MenuItemsData
import com.danilovfa.common.uikit.composables.popup.PopupMenu
import com.danilovfa.common.uikit.composables.stub.EmptyStub
import com.danilovfa.common.uikit.composables.text.Text
import com.danilovfa.common.uikit.composables.textfield.TextFieldLarge
import com.danilovfa.common.uikit.composables.toolbar.NavigationIcon
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.event.ObserveEvents
import com.danilovfa.common.uikit.modifier.surface
import com.danilovfa.common.uikit.preview.ThemePreviewParameter
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.domain.common.model.RecordingAnalysis
import com.danilovfa.presentation.patient.common.PatientCard
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.Intent
import com.danilovfa.presentation.patient.detail.store.PatientDetailStore.State
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
internal fun PatientDetailScreen(component: PatientDetailComponent) {
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogDelegate.alertDialogStateFlow.collectAsState()

    ObserveEvents(component.eventDelegate)

    PatientDetailLayout(
        state = state,
        onIntent = component::onIntent
    )

    AlertDialog(alertDialogState)
}

@Composable
private fun PatientDetailLayout(
    state: State,
    onIntent: (Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
    ) {
        PatientDetailToolbar(
            onBackClicked = { onIntent(Intent.OnBackClicked) },
            onDeleteClicked = { onIntent(Intent.OnDeletePatientClicked) },
            onExportClicked = { onIntent(Intent.OnExportClicked) },
            onEditClicked = { onIntent(Intent.OnEditPatientClicked) }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            PatientContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .fillMaxSize()
            )

            ButtonLarge(
                text = stringResource(strings.patient_add_analysis),
                onClick = { onIntent(Intent.OnRecordClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimension.layoutMainMargin)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PatientContent(
    state: State,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            VSpacer(AppDimension.layoutMainMargin)
            state.patient?.let { patient ->
                PatientCard(
                    patient = patient,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimension.layoutHorizontalMargin)
                )
            }
        }

        item {
            VSpacer(AppDimension.layoutMainMargin)
            PatientNote(
                note = state.note,
                onNoteChanged = { onIntent(Intent.OnNoteChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
            VSpacer(AppDimension.layoutMediumMargin)
        }

        stickyHeader {
            AnalysisSearch(
                query = state.searchQuery,
                onQueryChanged = { onIntent(Intent.OnSearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.background)
                    .padding(top = AppDimension.layoutMediumMargin)
                    .padding(horizontal = AppDimension.layoutHorizontalMargin)
            )
        }

        item {
            if (state.analyzes.isEmpty()) {
                EmptyStub(
                    title = stringResource(strings.patient_analyzes_empty_title),
                    message = stringResource(strings.patient_analyzes_empty_description),
                    fillMaxSize = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimension.layoutLargeMargin)
                )
            } else if (state.searchedAnalyzes.isEmpty()) {
                EmptyStub(
                    title = stringResource(strings.patient_analyzes_empty_title),
                    message = stringResource(strings.search_empty_description),
                    fillMaxSize = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppDimension.layoutLargeMargin)
                )
            }
        }

        if (state.searchedAnalyzes.isNotEmpty()) {
            items(
                items = state.searchedAnalyzes,
                key = { it.recording.id }
            ) { analysis ->
                AnalysisCard(
                    analysis = analysis,
                    onClick = { onIntent(Intent.OnAnalysisClicked(analysis)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = AppDimension.layoutHorizontalMargin,
                            vertical = AppDimension.layoutSmallMargin
                        )
                )
            }
        }


        item {
            VSpacer(AppDimension.minTouchSize + AppDimension.layoutMainMargin * 2)
        }
    }
}

@Composable
private fun AnalysisCard(
    analysis: RecordingAnalysis,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .surface(onClick = onClick)
            .padding(
                vertical = AppDimension.layoutMediumMargin,
                horizontal = AppDimension.layoutHorizontalMargin
            )

    ) {
//        Text(
//            text = analysis.recording.durationMillis.milliseconds.toSeconds(),
//            style = AppTypography.labelMedium12,
//            color = AppTheme.colors.textSecondary
//        )
        Text(
            text = analysis.recording.timestamp.toLocalDateTime(TimeZone.UTC).format { dateTime() },
            style = AppTypography.bodyRegular16,
            color = AppTheme.colors.textSecondary
        )
    }
}

@Composable
private fun AnalysisSearch(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextFieldLarge(
        value = query,
        onValueChange = onQueryChanged,
        labelText = stringResource(strings.search),
        leadingIcon = {
            Icon(
                painter = AppIcon.Search,
                tint = AppTheme.colors.textDisabled,
                contentDescription = "Search patient"
            )

        },
        modifier = modifier
    )
}

@Composable
private fun PatientNote(
    note: String,
    onNoteChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = note,
        onValueChange = onNoteChanged,
        modifier = modifier,
        singleLine = false,
        minLines = 3,
        textStyle = AppTypography.bodyRegular16,
        cursorBrush = SolidColor(AppTheme.colors.primary),
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .surface()
                    .padding(
                        vertical = AppDimension.layoutMediumMargin,
                        horizontal = AppDimension.layoutHorizontalMargin
                    )
            ) {
                Text(
                    text = stringResource(strings.patient_note),
                    style = AppTypography.bodyMedium16,
                    color = AppTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                VSpacer(AppDimension.layoutExtraSmallMargin)
                innerTextField()
            }
        }
    )
}

@Composable
private fun PatientDetailToolbar(
    onBackClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onExportClicked: () -> Unit,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Toolbar(
        title = stringResource(strings.patient),
        navigationIcon = NavigationIcon.Back,
        onNavigationClick = onBackClicked,
        actions = {
            val isPopupExpanded = remember { mutableStateOf(false) }

            val popupItems = arrayOf(
                MenuItemsData(
                    title = Text.Resource(strings.patient_edit_title),
                    icon = AppIcon.Edit,
                    tint = AppTheme.colors.primary,
                    onClick = onEditClicked,
                ),
                MenuItemsData(
                    title = Text.Resource(strings.export),
                    icon = AppIcon.Export,
                    tint = AppTheme.colors.primary,
                    onClick = onExportClicked,
                ),
                MenuItemsData(
                    title = Text.Resource(strings.patient_delete),
                    icon = AppIcon.Delete,
                    tint = AppTheme.colors.error,
                    onClick = onDeleteClicked,
                ),
            )

            Column(modifier) {
                IconButton(
                    onClick = { isPopupExpanded.value = true }
                ) {
                    Icon(
                        painter = AppIcon.VerticalMore,
                        tint = AppTheme.colors.primary,
                        contentDescription = "More"
                    )
                }
                PopupMenu(
                    menuItemsData = popupItems,
                    expandedState = isPopupExpanded
                )
            }
        }
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) isDark: Boolean) {
    AppTheme(isDark) {
        PatientDetailLayout(
            state = State(patientId = 0L),
            onIntent = {}
        )
    }
}