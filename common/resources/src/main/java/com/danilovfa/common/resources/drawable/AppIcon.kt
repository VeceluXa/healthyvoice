package com.danilovfa.common.resources.drawable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.danilovfa.resources.R

object AppIcon {
    val ArrowBack: Painter @Composable get() = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack)
    val VerticalMore: Painter @Composable get() = rememberVectorPainter(Icons.Filled.MoreVert)
    val ArrowRight: Painter @Composable get() = rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowRight)
    val Settings: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Settings)
    val Clear: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Clear)
    val Done: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Done)
    val Visibility: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Visibility)
    val VisibilityOff: Painter @Composable get() = rememberVectorPainter(Icons.Filled.VisibilityOff)
    val Check: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Check)
    val Close: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Close)
    val Warning: Painter @Composable get() = rememberVectorPainter(Icons.Filled.WarningAmber)
    val ReadAll: Painter @Composable get() = rememberVectorPainter(Icons.Outlined.MarkEmailRead)
    val ExpandMore: Painter @Composable get() = rememberVectorPainter(Icons.Filled.ExpandMore)
    val Add: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Add)
    val Delete: Painter @Composable get() = rememberVectorPainter(Icons.Outlined.Delete)
    val AppIcon: Painter @Composable get() = painterResource(R.drawable.icon_app)
    val Play: Painter @Composable get() = rememberVectorPainter(Icons.Filled.PlayArrow)
    val Pause: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Pause)
    val Stop: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Stop)
    val Question: Painter @Composable get() = rememberVectorPainter(Icons.Filled.QuestionMark)
    val ArrowUp: Painter @Composable get() = rememberVectorPainter(Icons.Filled.ArrowUpward)
    val Export: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Download)
    val SexMale: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Male)
    val SexFemale: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Female)
    val Search: Painter @Composable get() = rememberVectorPainter(Icons.Filled.Search)
}