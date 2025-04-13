package com.danilovfa.presentation.patient.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.date
import com.danilovfa.common.core.domain.time.format
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.stub.EmptyStub
import com.danilovfa.common.uikit.composables.textfield.TextFieldLarge
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.event.ObserveEvents
import com.danilovfa.common.uikit.modifier.surface
import com.danilovfa.common.uikit.preview.ThemePreviewParameter
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.common.PatientCard
import com.danilovfa.presentation.patient.common.iconPainter
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.State

@Composable
internal fun PatientListScreen(component: PatientListComponent) {
    val state by component.stateFlow.collectAsState()

    ObserveEvents(component.eventDelegate)

    PatientListLayout(
        state = state,
        onIntent = component::onIntent
    )
}

@Composable
private fun PatientListLayout(
    state: State,
    onIntent: (Intent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = AppTheme.colors.primary,
                shape = CircleShape,
                onClick = { onIntent(Intent.OnCreatePatientClicked) }
            ) {
                Icon(
                    painter = AppIcon.Add,
                    tint = AppTheme.colors.onPrimary,
                    contentDescription = "Create patient"
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .statusBarsPadding()
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            Toolbar(
                title = stringResource(strings.app_title),
                navigationIcon = null,
                onNavigationClick = {},
                actions = {
                    IconButton(
                        onClick = { onIntent(Intent.OnExportClicked) }
                    ) {
                        Icon(
                            painter = AppIcon.Export,
                            tint = AppTheme.colors.primary,
                            contentDescription = "Export"
                        )
                    }
                },
                contentBelowToolbar = {
                    TextFieldLarge(
                        value = state.searchQuery,
                        onValueChange = { onIntent(Intent.OnQueryChanged(it)) },
                        labelText = stringResource(strings.search),
                        leadingIcon = {
                            Icon(
                                painter = AppIcon.Search,
                                tint = AppTheme.colors.textDisabled,
                                contentDescription = "Search patient"
                            )

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppDimension.layoutHorizontalMargin)
                    )
                }
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    if (state.patients.isEmpty()) {
                        EmptyStub(
                            title = stringResource(strings.patient_list_empty_title),
                            message = stringResource(strings.patient_list_empty_description),
                            modifier = Modifier.fillParentMaxSize()
                        )
                    } else if (state.patientSearched.isEmpty()) {
                        EmptyStub(
                            title = stringResource(strings.patient_list_empty_title),
                            message = stringResource(strings.search_empty_description),
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }

                item {
                    VSpacer(AppDimension.layoutMainMargin)
                }

                items(
                    items = state.patientSearched,
                    key = { it.id }
                ) { patient ->
                    PatientCard(
                        patient = patient,
                        onClick = { onIntent(Intent.OnPatientClicked(patient)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AppDimension.layoutHorizontalMargin,
                                vertical = AppDimension.layoutSmallMargin
                            )
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) isDark: Boolean) {
    AppTheme(isDark) {
        PatientListLayout(
            state = State(),
            onIntent = {}
        )
    }
}