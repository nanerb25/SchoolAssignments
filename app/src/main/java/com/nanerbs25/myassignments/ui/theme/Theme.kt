package com.nanerbs25.myassignments.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

enum class ThemeMode {
    System, Light, Dark
}

private val Context.dataStore by preferencesDataStore(name = "theme_preferences")
val THEME_KEY = stringPreferencesKey("theme_key")

@Composable
fun MonetBasedTheming(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Retrieve the selected theme mode from DataStore
    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val themeValue = preferences[THEME_KEY] ?: ThemeMode.System.name
        ThemeMode.valueOf(themeValue)
    }

    val themeMode = themeModeFlow.collectAsState(initial = ThemeMode.System).value

    val darkTheme = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        if (darkTheme) {
            darkColorScheme()
        } else {
            lightColorScheme()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

suspend fun saveThemeMode(context: Context, themeMode: ThemeMode) {
    context.dataStore.edit { preferences ->
        preferences[THEME_KEY] = themeMode.name
    }
}

fun getSavedThemeMode(context: Context): ThemeMode {
    return runBlocking {
        val preferences = context.dataStore.data.first()
        val themeValue = preferences[THEME_KEY] ?: ThemeMode.System.name
        ThemeMode.valueOf(themeValue)
    }
}
