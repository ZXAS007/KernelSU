package me.weishu.kernelsu.ui.theme

import android.app.Activity
import com.microsoft.fluentui.theme.FluentTheme
import com.microsoft.fluentui.theme.ThemeMode
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import me.weishu.kernelsu.ui.webui.MonetColorsProvider

// Android's system sans-serif keeps all installed language glyphs available.
private val FluentTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
)

private val FluentShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(10.dp),
    extraLarge = RoundedCornerShape(12.dp),
)

@Composable
fun FluentKernelSUTheme(appSettings: AppSettings, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dark = appSettings.colorMode.isDark || (appSettings.colorMode.isSystem && isSystemInDarkTheme())
    val amoled = appSettings.colorMode.isAmoled
    val accentScheme = rememberKernelSUColorScheme(
        seedColor = if (appSettings.keyColor == 0) Color.Unspecified else Color(appSettings.keyColor),
        isDark = dark,
        isAmoled = amoled,
        paletteStyle = appSettings.paletteStyle,
        colorSpec = appSettings.colorSpec,
    )
    // Neutral layered surfaces with the user's existing accent and contrast pairs.
    val scheme = accentScheme.copy(
        background = if (amoled) Color.Black else if (dark) Color(0xFF202020) else Color(0xFFF3F3F3),
        surface = if (amoled) Color.Black else if (dark) Color(0xFF202020) else Color(0xFFF3F3F3),
        surfaceContainerLowest = if (amoled) Color.Black else if (dark) Color(0xFF181818) else Color.White,
        surfaceContainerLow = if (dark) Color(0xFF252525) else Color(0xFFFAFAFA),
        surfaceContainer = if (dark) Color(0xFF2C2C2C) else Color.White,
        surfaceContainerHigh = if (dark) Color(0xFF333333) else Color(0xFFF0F0F0),
        surfaceContainerHighest = if (dark) Color(0xFF3B3B3B) else Color(0xFFE8E8E8),
        onSurface = if (dark) Color(0xFFF5F5F5) else Color(0xFF242424),
        onBackground = if (dark) Color(0xFFF5F5F5) else Color(0xFF242424),
        onSurfaceVariant = if (dark) Color(0xFFD1D1D1) else Color(0xFF616161),
        outline = if (dark) Color(0xFF808080) else Color(0xFF767676),
        outlineVariant = if (dark) Color(0xFF454545) else Color(0xFFE0E0E0),
    )
    LaunchedEffect(dark) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !dark
            isAppearanceLightNavigationBars = !dark
        }
    }
    MaterialExpressiveTheme(
        colorScheme = scheme.animateAsState(),
        motionScheme = MotionScheme.standard(),
        typography = FluentTypography,
        shapes = FluentShapes,
    ) {
        MonetColorsProvider.UpdateCss(scheme)
        FluentTheme(themeMode = if (dark) ThemeMode.Dark else ThemeMode.Light) { content() }
    }
}
