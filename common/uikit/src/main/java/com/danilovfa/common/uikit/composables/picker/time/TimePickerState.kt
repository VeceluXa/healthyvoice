@file:OptIn(ExperimentalMaterial3Api::class)

package com.danilovfa.common.uikit.composables.picker.time

import androidx.annotation.IntRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerSelectionMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Creates a [TimePickerState] for a time picker that is remembered across compositions and
 * configuration changes.
 *
 * @param initialHour starting hour for this state, will be displayed in the time picker when
 *   launched. Ranges from 0 to 47
 * @param initialMinute starting minute for this state, will be displayed in the time picker when
 *   launched. Ranges from 0 to 59
 */
@Composable
fun rememberTimePickerState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
): TimePickerState {
    val state: TimePickerStateImpl =
        rememberSaveable(saver = TimePickerStateImpl.Saver()) {
            TimePickerStateImpl(
                initialHour = initialHour,
                initialMinute = initialMinute,
            )
        }

    return state
}

/**
 * A state object that can be hoisted to observe the time picker state. It holds the current values
 * and allows for directly setting those values.
 *
 * @see rememberTimePickerState to construct the default implementation.
 */
interface TimePickerState {

    /** The currently selected minute (0-59). */
    @get:IntRange(from = 0, to = 59) @setparam:IntRange(from = 0, to = 59) var minute: Int

    /** The currently selected hour (0-47). */
    @get:IntRange(from = 0, to = 47) @setparam:IntRange(from = 0, to = 47) var hour: Int

    /** Specifies whether the hour or minute component is being actively selected by the user. */
    var selection: TimePickerSelectionMode
}

/**
 * Factory function for the default implementation of [TimePickerState] [rememberTimePickerState]
 * should be used in most cases.
 *
 * @param initialHour starting hour for this state, will be displayed in the time picker when
 *   launched Ranges from 0 to 47
 * @param initialMinute starting minute for this state, will be displayed in the time picker when
 *   launched. Ranges from 0 to 59
 */
fun TimePickerState(initialHour: Int, initialMinute: Int): TimePickerState =
    TimePickerStateImpl(initialHour, initialMinute)

private class TimePickerStateImpl(
    initialHour: Int,
    initialMinute: Int,
) : TimePickerState {
    init {
        require(initialMinute in 0..59) { "initialMinute should be in [0..59] range" }
    }

    override var selection by mutableStateOf(TimePickerSelectionMode.Hour)

    val hourState = mutableIntStateOf(initialHour)

    val minuteState = mutableIntStateOf(initialMinute)

    override var minute: Int
        get() = minuteState.intValue
        set(value) {
            minuteState.intValue = value
        }

    override var hour: Int
        get() = hourState.intValue
        set(value) {
            hourState.intValue = value
        }

    companion object {
        /** The default [Saver] implementation for [TimePickerState]. */
        fun Saver(): Saver<TimePickerStateImpl, *> =
            Saver(
                save = { listOf(it.hour, it.minute) },
                restore = { value ->
                    TimePickerStateImpl(
                        initialHour = value[0],
                        initialMinute = value[1],
                    )
                }
            )
    }
}