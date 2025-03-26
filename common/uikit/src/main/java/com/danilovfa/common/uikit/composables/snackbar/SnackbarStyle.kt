package com.danilovfa.common.uikit.composables.snackbar

import android.graphics.Bitmap
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.danilovfa.common.uikit.theme.AppTheme
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

interface SnackbarStyle {
    val icon: Painter?
        @Composable get() = null
    val shape: Shape
        @Composable get() = RoundedCornerShape(0.dp)

    val backgroundColor: Color
        @Composable get() = AppTheme.colors.surface

    val contentColor: Color
        @Composable get() = AppTheme.colors.onSurface

    object Default : SnackbarStyle

    object Error : SnackbarStyle {
        override val backgroundColor: Color
            @Composable get() = AppTheme.colors.error

        override val contentColor: Color
            @Composable get() = AppTheme.colors.onError
    }
}

@Composable
private fun StyledSnackbar(snackbarData: SnackbarData) {
    if (snackbarData is StyledSnackbarData) {
        Snackbar(snackbarData = snackbarData)
    } else {
        Snackbar(snackbarData)
    }
}

internal class StyledSnackbarData(
    val message: String,
    val image: Bitmap?,
    val actionLabel: String?,
    val duration: SnackbarDuration,
    val style: SnackbarStyle,
    private val continuation: CancellableContinuation<SnackbarResult>
) : SnackbarData {

    override val visuals: SnackbarVisuals = AppSnackbarVisuals(
        message = message,
        actionLabel = actionLabel,
        duration = duration,
        withDismissAction = false
    )

    override fun dismiss() {
        if (continuation.isActive) continuation.resume(SnackbarResult.Dismissed)
    }

    override fun performAction() {
        if (continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed)
    }

    class AppSnackbarVisuals(
        override val message: String,
        override val actionLabel: String?,
        override val withDismissAction: Boolean,
        override val duration: SnackbarDuration
    ) : SnackbarVisuals {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as AppSnackbarVisuals

            if (message != other.message) return false
            if (actionLabel != other.actionLabel) return false
            if (withDismissAction != other.withDismissAction) return false
            if (duration != other.duration) return false

            return true
        }

        override fun hashCode(): Int {
            var result = message.hashCode()
            result = 31 * result + actionLabel.hashCode()
            result = 31 * result + withDismissAction.hashCode()
            result = 31 * result + duration.hashCode()
            return result
        }
    }
}