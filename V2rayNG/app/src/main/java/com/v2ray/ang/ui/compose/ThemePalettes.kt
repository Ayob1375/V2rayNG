package com.v2ray.ang.ui.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R

/**
 * Palette definitions for the theme style picker.
 * Each palette keeps the app's neutral surface system and swaps the accent roles,
 * so the whole app stays coherent no matter which style is selected.
 */
enum class ThemePalette(
    val id: String,
    @StringRes val labelRes: Int,
    val light: androidx.compose.material3.ColorScheme,
    val dark: androidx.compose.material3.ColorScheme,
) {
    DEFAULT(
        "0", R.string.theme_style_default,
        light = lightAccentScheme(
            primary = Color(0xFF000000), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE0E0E0), onPrimaryContainer = Color(0xFF000000),
            secondary = Color(0xFFF97910), onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFE8D6), onSecondaryContainer = Color(0xFF2B1700),
            tertiary = Color(0xFF009966), onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFA0F2D0), onTertiaryContainer = Color(0xFF00201A),
        ),
        dark = darkAccentScheme(
            primary = Color(0xFFC0C0C0), onPrimary = Color(0xFF303030),
            primaryContainer = Color(0xFF474747), onPrimaryContainer = Color(0xFFE0E0E0),
            secondary = Color(0xFFF97910), onSecondary = Color(0xFF4E2600),
            secondaryContainer = Color(0xFF6F3800), onSecondaryContainer = Color(0xFFFFE8D6),
            tertiary = Color(0xFF83D6B5), onTertiary = Color(0xFF00382E),
            tertiaryContainer = Color(0xFF005143), onTertiaryContainer = Color(0xFFA0F2D0),
        ),
    ),
    OCEAN(
        "1", R.string.theme_style_ocean,
        light = lightAccentScheme(
            primary = Color(0xFF1565C0), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFD3E4FF), onPrimaryContainer = Color(0xFF001C3A),
            secondary = Color(0xFF00838F), onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFB4EBF2), onSecondaryContainer = Color(0xFF001F24),
            tertiary = Color(0xFF3949AB), onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFDCE0FF), onTertiaryContainer = Color(0xFF000F5C),
        ),
        dark = darkAccentScheme(
            primary = Color(0xFF9ECAFF), onPrimary = Color(0xFF00315E),
            primaryContainer = Color(0xFF00497F), onPrimaryContainer = Color(0xFFD3E4FF),
            secondary = Color(0xFF4DD8E9), onSecondary = Color(0xFF00363C),
            secondaryContainer = Color(0xFF004F57), onSecondaryContainer = Color(0xFFB4EBF2),
            tertiary = Color(0xFFBAC3FF), onTertiary = Color(0xFF0A1D7E),
            tertiaryContainer = Color(0xFF24399A), onTertiaryContainer = Color(0xFFDCE0FF),
        ),
    ),
    NEON_PURPLE(
        "2", R.string.theme_style_neon,
        light = lightAccentScheme(
            primary = Color(0xFF6A3AB2), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFEBDCFF), onPrimaryContainer = Color(0xFF23005B),
            secondary = Color(0xFFC2185B), onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFD9E2), onSecondaryContainer = Color(0xFF3E001D),
            tertiary = Color(0xFF00897B), onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFB2DFDB), onTertiaryContainer = Color(0xFF00201C),
        ),
        dark = darkAccentScheme(
            primary = Color(0xFFD4BAFF), onPrimary = Color(0xFF3B008F),
            primaryContainer = Color(0xFF531BA9), onPrimaryContainer = Color(0xFFEBDCFF),
            secondary = Color(0xFFFFB1C8), onSecondary = Color(0xFF650031),
            secondaryContainer = Color(0xFF8E0049), onSecondaryContainer = Color(0xFFFFD9E2),
            tertiary = Color(0xFF82CBC4), onTertiary = Color(0xFF00201C),
            tertiaryContainer = Color(0xFF005048), onTertiaryContainer = Color(0xFFB2DFDB),
        ),
    ),
    EMERALD(
        "3", R.string.theme_style_emerald,
        light = lightAccentScheme(
            primary = Color(0xFF00695C), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFB2DFDB), onPrimaryContainer = Color(0xFF00201C),
            secondary = Color(0xFF558B2F), onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFDCEDC8), onSecondaryContainer = Color(0xFF1E3701),
            tertiary = Color(0xFF0277BD), onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFB3E5FC), onTertiaryContainer = Color(0xFF001F2A),
        ),
        dark = darkAccentScheme(
            primary = Color(0xFF80CBC4), onPrimary = Color(0xFF003733),
            primaryContainer = Color(0xFF005048), onPrimaryContainer = Color(0xFFB2DFDB),
            secondary = Color(0xFFAED581), onSecondary = Color(0xFF2E4900),
            secondaryContainer = Color(0xFF406800), onSecondaryContainer = Color(0xFFDCEDC8),
            tertiary = Color(0xFF81D4FA), onTertiary = Color(0xFF003549),
            tertiaryContainer = Color(0xFF004B6B), onTertiaryContainer = Color(0xFFB3E5FC),
        ),
    ),
    SUNSET(
        "4", R.string.theme_style_sunset,
        light = lightAccentScheme(
            primary = Color(0xFFBF360C), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFDBCF), onPrimaryContainer = Color(0xFF3D0000),
            secondary = Color(0xFFEF6C00), onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFE0B2), onSecondaryContainer = Color(0xFF4E2600),
            tertiary = Color(0xFFAFB42B), onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFF0F4C3), onTertiaryContainer = Color(0xFF1F2600),
        ),
        dark = darkAccentScheme(
            primary = Color(0xFFFFB59D), onPrimary = Color(0xFF5F1500),
            primaryContainer = Color(0xFF8D2F00), onPrimaryContainer = Color(0xFFFFDBCF),
            secondary = Color(0xFFFFB86B), onSecondary = Color(0xFF4E2600),
            secondaryContainer = Color(0xFF6F3800), onSecondaryContainer = Color(0xFFFFE0B2),
            tertiary = Color(0xFFE6EE9C), onTertiary = Color(0xFF1F2600),
            tertiaryContainer = Color(0xFF464D00), onTertiaryContainer = Color(0xFFF0F4C3),
        ),
    ),
    ROSE(
        "5", R.string.theme_style_rose,
        light = lightAccentScheme(
            primary = Color(0xFFAD2E5A), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFD9E1), onPrimaryContainer = Color(0xFF3F0019),
            secondary = Color(0xFF8E24AA), onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFF3DAFF), onSecondaryContainer = Color(0xFF2E004B),
            tertiary = Color(0xFFC62828), onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFDAD6), onTertiaryContainer = Color(0xFF410002),
        ),
        dark = darkAccentScheme(
            primary = Color(0xFFFFB1C4), onPrimary = Color(0xFF650031),
            primaryContainer = Color(0xFF8A1148), onPrimaryContainer = Color(0xFFFFD9E1),
            secondary = Color(0xFFE3B8F5), onSecondary = Color(0xFF470F60),
            secondaryContainer = Color(0xFF602C79), onSecondaryContainer = Color(0xFFF3DAFF),
            tertiary = Color(0xFFFFB4AB), onTertiary = Color(0xFF690005),
            tertiaryContainer = Color(0xFF93000A), onTertiaryContainer = Color(0xFFFFDAD6),
        ),
    ),
    ;

    companion object {
        fun fromId(id: String?): ThemePalette =
            entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}

/** Shared neutral roles for light palettes (identical to the stock light scheme). */
private fun lightAccentScheme(
    primary: Color, onPrimary: Color,
    primaryContainer: Color, onPrimaryContainer: Color,
    secondary: Color, onSecondary: Color,
    secondaryContainer: Color, onSecondaryContainer: Color,
    tertiary: Color, onTertiary: Color,
    tertiaryContainer: Color, onTertiaryContainer: Color,
) = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFC0C0C0),
    scrim = Color(0xFF000000),
    surfaceTint = primary,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF1F1F1),
    surfaceContainerHigh = Color(0xFFEBEBEB),
    surfaceContainerHighest = Color(0xFFE5E5E5),
)

/** Shared neutral roles for dark palettes (identical to the stock dark scheme). */
private fun darkAccentScheme(
    primary: Color, onPrimary: Color,
    primaryContainer: Color, onPrimaryContainer: Color,
    secondary: Color, onSecondary: Color,
    secondaryContainer: Color, onSecondaryContainer: Color,
    tertiary: Color, onTertiary: Color,
    tertiaryContainer: Color, onTertiaryContainer: Color,
) = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = Color(0xFF000000),
    scrim = Color(0xFF000000),
    surfaceTint = primary,
    surfaceContainerLowest = Color(0xFF0F0F12),
    surfaceContainerLow = Color(0xFF1A191D),
    surfaceContainer = Color(0xFF1E1D21),
    surfaceContainerHigh = Color(0xFF282729),
    surfaceContainerHighest = Color(0xFF333234),
)

/**
 * Visual palette picker shown in Settings > UI settings.
 * Renders one gradient swatch per palette with a check mark on the selected one.
 */
@Composable
fun ThemeStylePicker(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit,
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ThemePalette.entries.forEach { palette ->
                val selected = palette.id == selectedStyle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onStyleSelected(palette.id) }
                        .padding(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        palette.light.primary,
                                        palette.light.secondary,
                                    )
                                )
                            )
                            .border(
                                width = if (selected) 3.dp else 1.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary else borderColor,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Icon(
                                painter = painterResource(R.drawable.ic_action_done),
                                contentDescription = stringResource(palette.labelRes),
                                tint = Color.White,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                    Text(
                        text = stringResource(palette.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}
