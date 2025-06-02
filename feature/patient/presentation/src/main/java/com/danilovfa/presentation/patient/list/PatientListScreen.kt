package com.danilovfa.presentation.patient.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.danilovfa.common.core.presentation.Text
import com.danilovfa.common.core.presentation.extensions.launchAppSettings
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.common.uikit.composables.VSpacer
import com.danilovfa.common.uikit.composables.dialog.AlertDialog
import com.danilovfa.common.uikit.composables.popup.MenuItemsData
import com.danilovfa.common.uikit.composables.popup.PopupMenu
import com.danilovfa.common.uikit.composables.stub.EmptyStub
import com.danilovfa.common.uikit.composables.textfield.TextFieldLarge
import com.danilovfa.common.uikit.composables.toolbar.Toolbar
import com.danilovfa.common.uikit.event.ObserveEvents
import com.danilovfa.common.uikit.event.ObserveRequestPermissionEvents
import com.danilovfa.common.uikit.preview.ThemePreviewParameter
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.domain.common.model.Patient
import com.danilovfa.presentation.patient.common.PatientCard
import com.danilovfa.presentation.patient.list.PatientListComponent.PatientListEvents
import com.danilovfa.presentation.patient.list.store.PatientListStore.Intent
import com.danilovfa.presentation.patient.list.store.PatientListStore.State

@Composable
internal fun PatientListScreen(component: PatientListComponent) {
    val state by component.stateFlow.collectAsState()
    val alertDialogState by component.alertDialogDelegate.alertDialogStateFlow.collectAsState()

    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            component.onIntent(Intent.RequestNotificationPermission)
        }

    LaunchedEffect(Unit) {
        component.onIntent(Intent.RequestNotificationPermission)
    }

    ObserveEvents(component.eventDelegate) { event ->
        when (event) {
            is PatientListEvents.LaunchAppSettings -> {
                launcher.launchAppSettings(context)
                true
            }

            else -> false
        }
    }

    ObserveRequestPermissionEvents(component.requestPermissionEventDelegate.requestPermissionFlow) {
        component.onIntent(Intent.OnNotificationPermissionRequested(it))
    }

    PatientListLayout(
        state = state,
        onIntent = component::onIntent
    )

    AlertDialog(alertDialogState)
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
                searchQuery = state.searchQuery,
                onSearchQueryChanged = { onIntent(Intent.OnQueryChanged(it)) },
                onDeleteClicked = { onIntent(Intent.OnDeleteClicked) },
                onExportClicked = { onIntent(Intent.OnExportClicked) }
            )

            PatientListContent(
                patients = state.patients,
                patientSearched = state.patientSearched,
                onPatientClicked = { onIntent(Intent.OnPatientClicked(it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PatientListContent(
    patients: List<Patient>,
    patientSearched: List<Patient>,
    onPatientClicked: (Patient) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        item {
            if (patients.isEmpty()) {
                EmptyStub(
                    title = stringResource(strings.patient_list_empty_title),
                    message = stringResource(strings.patient_list_empty_description),
                    modifier = Modifier.fillParentMaxSize()
                )
            } else if (patientSearched.isEmpty()) {
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
            items = patientSearched,
            key = { it.id }
        ) { patient ->
            PatientCard(
                patient = patient,
                onClick = { onPatientClicked(patient) },
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

@Composable
private fun Toolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onExportClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Toolbar(
        title = stringResource(strings.app_title),
        navigationIcon = null,
        onNavigationClick = {},
        modifier = modifier,
        actions = {
            val isPopupExpanded = remember { mutableStateOf(false) }

            val popupItems = arrayOf(
                MenuItemsData(
                    title = Text.Resource(strings.export),
                    icon = AppIcon.Export,
                    tint = AppTheme.colors.primary,
                    onClick = onExportClicked,
                ),
                MenuItemsData(
                    title = Text.Resource(strings.patient_delete_all),
                    icon = AppIcon.Delete,
                    tint = AppTheme.colors.error,
                    onClick = onDeleteClicked,
                ),
            )

            Column {
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
        },
        contentBelowToolbar = {
            TextFieldLarge(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
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