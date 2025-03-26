package com.danilovfa.common.uikit.composables.picker.date

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import com.danilovfa.common.core.domain.time.utcToLocal
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
internal class SelectableDatesImpl(
    private val selectableDates: List<LocalDate>
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return Instant.fromEpochMilliseconds(utcTimeMillis)
            .toLocalDateTime(TimeZone.UTC)
            .utcToLocal().date in selectableDates
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year in selectableDates.map { it.year }
    }
}