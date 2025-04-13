package com.danilovfa.presentation.patient.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danilovfa.common.core.domain.time.KotlinDateTimeFormatters.date
import com.danilovfa.common.core.domain.time.format
import com.danilovfa.common.uikit.composables.HSpacer
import com.danilovfa.common.uikit.modifier.surface
import com.danilovfa.common.uikit.theme.AppDimension
import com.danilovfa.common.uikit.theme.AppTheme
import com.danilovfa.common.uikit.theme.AppTypography
import com.danilovfa.domain.common.model.Patient

@Composable
internal fun PatientCard(
    patient: Patient,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .surface(
                onClick = onClick
            )
            .padding(
                horizontal = AppDimension.layoutHorizontalMargin,
                vertical = AppDimension.layoutMediumMargin
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = patient.name,
                style = AppTypography.titleRegular18,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = patient.birthDate.format { date() },
                style = AppTypography.bodyRegular16,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        HSpacer(AppDimension.layoutSmallMargin)

        Icon(
            painter = patient.sex.iconPainter,
            tint = AppTheme.colors.textDisabled,
            contentDescription = "Patient sex",
        )
    }
}