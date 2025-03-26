package com.danilovfa.presentation.cut.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.danilovfa.presentation.cut.store.CutStoreExecutor.Companion.MIN_CUT_SIZE_MILLIS
import com.danilovfa.common.uikit.theme.AppTheme
import kotlin.math.abs
import kotlin.math.roundToInt

private const val GRAPH_HEIGHT_DP = 200
private const val CUT_BORDER_WIDTH_DP = 3

@Composable
fun CutGraph(
    amplitudes: List<Float>,
    startOffsetMillis: Int,
    endOffsetMillis: Int,
    durationMillis: Int,
    onStartOffsetChanged: (Int) -> Unit,
    onEndOffsetChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val screenWidthPx = with (density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val graphWidthPx = screenWidthPx

    val minCutPx = graphWidthPx / durationMillis * MIN_CUT_SIZE_MILLIS

    val amplitudeWidthPx = (graphWidthPx / amplitudes.size) * 2 / 3
    val distanceBetweenAmplitudesPx = (graphWidthPx / amplitudes.size) / 3
    val amplitudeColor = AppTheme.colors.primary

    val cutColor = AppTheme.colors.primary.copy(alpha = 0.4f)

    var startOffsetPx by remember {
        mutableFloatStateOf(graphWidthPx / durationMillis * startOffsetMillis)
    }

    var endOffsetPx by remember {
        mutableFloatStateOf(graphWidthPx / durationMillis * endOffsetMillis)
    }

    LaunchedEffect(startOffsetPx) {
        val newStartOffsetMillis = (startOffsetPx / graphWidthPx * durationMillis).roundToInt()
        onStartOffsetChanged(newStartOffsetMillis)
    }

    LaunchedEffect(endOffsetPx) {
        val newEndOffsetMillis = (endOffsetPx / graphWidthPx * durationMillis).roundToInt()
        onEndOffsetChanged(newEndOffsetMillis)
    }

    Canvas(
        modifier = modifier
            .height(GRAPH_HEIGHT_DP.dp)
            .fillMaxWidth()
            .drawBehind {
                drawCutBackground(
                    startOffsetPx = startOffsetPx - (CUT_BORDER_WIDTH_DP.toFloat() / 2).dp.toPx(),
                    endOffsetPx = endOffsetPx + (CUT_BORDER_WIDTH_DP.toFloat() / 2).dp.toPx(),
                    color = cutColor.copy(alpha = 0.5f)
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val distanceToStart = abs(startOffsetPx - change.position.x)
                    val distanceToEnd = abs(endOffsetPx - change.position.x)

                    if (distanceToStart < distanceToEnd) {
                        val newStartOffsetPx = startOffsetPx + dragAmount
                        if (endOffsetPx - newStartOffsetPx > minCutPx && newStartOffsetPx > 0) {
                            startOffsetPx = newStartOffsetPx
                        }
                    } else {
                        val newEndOffsetPx = endOffsetPx + dragAmount
                        if (newEndOffsetPx - startOffsetPx > minCutPx && newEndOffsetPx <= graphWidthPx) {
                            endOffsetPx = newEndOffsetPx
                        }

                    }
                }
            }
    ) {
        amplitudes.forEachIndexed { i, amplitude ->
            drawAmplitude(
                widthPx = amplitudeWidthPx,
                amplitude = amplitude,
                color = amplitudeColor,
                index = i,
                distanceBetweenPx = distanceBetweenAmplitudesPx
            )
        }

        drawCutBorder(
            offsetPx = startOffsetPx,
            color = cutColor
        )

        drawCutBorder(
            offsetPx = endOffsetPx,
            color = cutColor
        )
    }
}

private fun DrawScope.drawCutBorder(
    offsetPx: Float,
    color: Color
) {
    drawLine(
        brush = SolidColor(color),
        start = Offset(
            x = offsetPx,
            y = 0f,
        ),
        end = Offset(
            x = offsetPx,
            y = GRAPH_HEIGHT_DP.dp.toPx()
        ),
        strokeWidth = CUT_BORDER_WIDTH_DP.dp.toPx()
    )
}

private fun DrawScope.drawAmplitude(
    widthPx: Float,
    amplitude: Float,
    color: Color,
    index: Int,
    distanceBetweenPx: Float,
) {
    val amplitudeHeight = GRAPH_HEIGHT_DP.dp.toPx() * amplitude

    drawRoundRect(
        color = color,
        topLeft = Offset(
            x = (widthPx + distanceBetweenPx) * index,
            y = (GRAPH_HEIGHT_DP.dp.toPx() / 2) - (amplitudeHeight / 2)
        ),
        size = Size(
            width = widthPx,
            height = amplitudeHeight
        ),
        cornerRadius = CornerRadius(x = 100f, y = 100f)
    )
}

private fun DrawScope.drawCutBackground(
    startOffsetPx: Float,
    endOffsetPx: Float,
    color: Color
) {
    drawRect(
        brush = SolidColor(color),
        topLeft = Offset(
            x = startOffsetPx,
            y = 0f
        ),
        size = Size(
            width = endOffsetPx - startOffsetPx,
            height = GRAPH_HEIGHT_DP.dp.toPx()
        )
    )
}