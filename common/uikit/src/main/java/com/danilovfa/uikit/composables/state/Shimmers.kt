package com.danilovfa.uikit.composables.state

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.danilovfa.uikit.composables.VSpacer
import com.danilovfa.uikit.theme.AppTheme
import com.danilovfa.uikit.theme.largeShimmer
import com.danilovfa.uikit.theme.micro

@Composable
fun ShimmerItem(
    size: DpSize,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.micro,
) {
    Box(
        modifier = modifier
            .size(size.width, size.height)
            .background(getShimmerBrush(), shape),
    )
}

@Composable
fun LargeShimmerItem(
    height: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.largeShimmer,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(getShimmerBrush(), shape),
    )
}

@Composable
fun CircleShimmerItem(
    shape: Shape = CircleShape,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(getShimmerBrush(), shape),
    )
}

@Composable
fun CheckBoxShimmerItem() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(getShimmerBrush(), CircleShape)
            .padding(2.dp)
            .background(AppTheme.colors.backgroundPrimary, CircleShape),
    )
}

@Suppress("MagicNumber")
@Composable
private fun getShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0F,
        targetValue = 1000F,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAnimation"
    )

    return Brush.linearGradient(
        colors = getShimmerColors(),
        start = Offset(10F, 10F),
        end = Offset(translateAnim, translateAnim)
    )
}

@Composable
private fun getShimmerColors() = listOf(
    AppTheme.colors.shimmerGradient.colorStart,
    AppTheme.colors.shimmerGradient.colorCenter,
    AppTheme.colors.shimmerGradient.colorEnd,
)

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Column(Modifier.background(AppTheme.colors.backgroundPrimary)) {
            ShimmerItem(size = DpSize(100.dp, 32.dp))
            VSpacer(16.dp)
            LargeShimmerItem(height = 40.dp)
            VSpacer(size = 16.dp)
            CircleShimmerItem()
            VSpacer(size = 16.dp)
            CheckBoxShimmerItem()
        }
    }
}