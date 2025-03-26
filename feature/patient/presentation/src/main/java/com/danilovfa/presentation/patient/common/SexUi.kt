package com.danilovfa.presentation.patient.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.danilovfa.common.resources.drawable.AppIcon
import com.danilovfa.common.resources.strings
import com.danilovfa.domain.common.model.Sex

internal val Sex.textRes: Int @StringRes get() = when (this) {
    Sex.MALE -> strings.patient_sex_male
    Sex.FEMALE -> strings.patient_sex_female
}

internal val Sex.iconPainter: Painter @Composable get() = when (this) {
    Sex.MALE -> AppIcon.SexMale
    Sex.FEMALE -> AppIcon.SexFemale
}