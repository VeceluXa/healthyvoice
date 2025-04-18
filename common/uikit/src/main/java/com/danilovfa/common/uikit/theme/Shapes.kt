package com.danilovfa.common.uikit.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val shapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

val Shapes.largeIcon: CornerBasedShape
    get() = RoundedCornerShape(24.dp)

val Shapes.largeBanner: CornerBasedShape
    get() = RoundedCornerShape(20.dp)

val Shapes.largeBlock: CornerBasedShape
    get() = RoundedCornerShape(16.dp)

val Shapes.largeShimmer: CornerBasedShape
    get() = RoundedCornerShape(12.dp)

val Shapes.tiny: CornerBasedShape
    get() = RoundedCornerShape(6.dp)

val Shapes.micro: CornerBasedShape
    get() = RoundedCornerShape(4.dp)

val Shapes.textShimmer: CornerBasedShape
    get() = RoundedCornerShape(2.dp)

val Shapes.image: CornerBasedShape
    get() = RoundedCornerShape(8.dp)

@Preview(showBackground = true)
@Composable
private fun ShapesPreview() {
    Column {
        Row {
            ShapePreview(shapes.small, name = "small")
            ShapePreview(shapes.medium, name = "medium")
            ShapePreview(shapes.large, name = "large")
            ShapePreview(shapes.tiny, name = "tiny")
        }
    }
}

@Composable
private fun ShapePreview(shape: Shape, name: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color.Gray, shape)
            .padding(10.dp)
    ) {
        Text(name)
    }
}
