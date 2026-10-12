package me.weishu.kernelsu.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import me.weishu.kernelsu.ui.webui.MonetColorsProvider

@Composable
fun FluentKernelSUTheme(appSettings: AppSettings, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dark = appSettings.colorMode.isDark || (appSettings.colorMode.isSystem && isSystemInDarkTheme())
    val scheme = rememberKernelSUColorScheme(seedColor = if (appSettings.keyColor == 0) Color.Unspecified else Color(appSettings.keyColor), isDark = dark, isAmoled = appSettings.colorMode.isAmoled, paletteStyle = appSettings.paletteStyle, colorSpec = appSettings.colorSpec)
    LaunchedEffect(dark) { val w = (context as? Activity)?.window ?: return@LaunchedEffect; WindowInsetsControllerCompat(w, w.decorView).apply { isAppearanceLightStatusBars = !dark; isAppearanceLightNavigationBars = !dark } }
    MaterialExpressiveTheme(colorScheme = scheme.animateAsState(), motionScheme = MotionScheme.expressive(), typography = Typography, shapes = Shapes(extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp), small = androidx.compose.foundation.shape.RoundedCornerShape(6.dp), medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp), large = androidx.compose.foundation.shape.RoundedCornerShape(10.dp), extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)), content = { MonetColorsProvider.UpdateCss(scheme); content() })
}
