package com.danilovfa.core.library.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun tickerInstant(delay: Long = 5000): State<Instant> {
    val state = remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(Unit){
        while (true){
            delay(delay)
            state.value = Clock.System.now()
        }
    }
    return state
}

fun LocalDateTime.format(formatter: DateTimeFormatter): String = this
    .toJavaLocalDateTime()
    .format(formatter)