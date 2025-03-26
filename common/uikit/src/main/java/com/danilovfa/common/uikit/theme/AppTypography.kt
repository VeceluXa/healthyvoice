package com.danilovfa.common.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val materialTypography: Typography = Typography(
    displayLarge = AppTypography.displayRegular57,
    displayMedium = AppTypography.displayRegular45,
    displaySmall = AppTypography.displayRegular36,
    headlineLarge = AppTypography.headlineRegular32,
    headlineMedium = AppTypography.headlineRegular28,
    headlineSmall = AppTypography.headlineRegular24,
    titleLarge = AppTypography.titleRegular22,
    titleMedium = AppTypography.titleRegular20,
    titleSmall = AppTypography.titleRegular18,
    bodyLarge = AppTypography.bodyRegular16,
    bodyMedium = AppTypography.bodyRegular14,
    bodySmall = AppTypography.bodyRegular12,
    labelLarge = AppTypography.labelMedium14,
    labelMedium = AppTypography.labelRegular12,
    labelSmall = AppTypography.labelMedium11
)

object AppTypography {
    private val defaultStyle = TextStyle(
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )

    /**  Display   */

    val displayRegular57 = defaultStyle.copy(fontSize = 57.sp)
    val displayRegular45 = defaultStyle.copy(fontSize = 45.sp)
    val displayRegular36 = defaultStyle.copy(fontSize = 36.sp)

    /**  Headline   */

    val headlineRegular32 = defaultStyle.copy(fontSize = 32.sp)
    val headlineRegular28 = defaultStyle.copy(fontSize = 28.sp)
    val headlineRegular24 = defaultStyle.copy(fontSize = 24.sp)

    /**  Title   */

    val titleRegular22 = defaultStyle.copy(fontSize = 22.sp)
    val titleMedium22 = titleRegular22.copy(fontWeight = FontWeight.Medium)

    val titleRegular20 = defaultStyle.copy(fontSize = 20.sp)
    val titleMedium20 = titleRegular20.copy(fontSize = 20.sp)

    val titleRegular18 = defaultStyle.copy(fontSize = 18.sp)
    val titleMedium18 = titleRegular18.copy(fontWeight = FontWeight.Medium)

    /**  Body   */

    val bodyRegular16 = defaultStyle.copy(fontSize = 16.sp)
    val bodyMedium16 = bodyRegular16.copy(fontWeight = FontWeight.Medium)

    val bodyRegular14 = defaultStyle.copy(fontSize = 14.sp)
    val bodyRegular12 = defaultStyle.copy(fontSize = 12.sp)

    /**  Label   */

    val labelRegular14 = defaultStyle.copy(fontSize = 14.sp)
    val labelMedium14 = labelRegular14.copy(fontWeight = FontWeight.Medium)

    val labelRegular12 = defaultStyle.copy(fontSize = 12.sp)
    val labelMedium12 = defaultStyle.copy(fontWeight = FontWeight.Medium)

    val labelRegular11 = defaultStyle.copy(fontSize = 11.sp)
    val labelMedium11 = defaultStyle.copy(fontWeight = FontWeight.Medium)
}
