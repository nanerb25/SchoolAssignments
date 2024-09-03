package com.nanerbs25.myassignments

// AboutActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nanerbs25.myassignments.ui.theme.MonetBasedTheming
import com.nanerbs25.myassignments.CardButton
import com.nanerbs25.myassignments.ui.theme.ThemeMode
import com.nanerbs25.myassignments.ui.theme.getSavedThemeMode
import com.nanerbs25.myassignments.ui.theme.saveThemeMode
import kotlinx.coroutines.launch

class DisplaySettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonetBasedTheming {
                DisplaySettings(onBackClick = { finish() }) // Call finish() to close the activity
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplaySettings(onBackClick: () -> Unit) {
    // Status Bar Color
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.background
    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor)
    }
    val context = LocalContext.current
    // State to manage the dialog
    val themeOptions = listOf(ThemeMode.System, ThemeMode.Light, ThemeMode.Dark)
    val scope = rememberCoroutineScope()
    var isDialogOpen by remember { mutableStateOf(false) }
    val selectedTheme = remember { mutableStateOf(getSavedThemeMode(context)) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Display Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor= MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            CardButton(
                onClick = { isDialogOpen = true },
                title = "Change Theme",
                description = "Change the theme of the app"
            )

            // Add more content as needed
        }
    }
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    themeOptions.forEach { theme ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTheme.value = theme
                                    scope.launch {
                                        saveThemeMode(context, theme)
                                    }
                                    isDialogOpen = false
                                }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = (theme == selectedTheme.value),
                                onClick = {
                                    selectedTheme.value = theme
                                    scope.launch {
                                        saveThemeMode(context, theme)
                                    }
                                    isDialogOpen = false
                                }
                            )
                            Text(theme.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isDialogOpen = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DisplaySettingsScreenPreview() {
    DisplaySettings(onBackClick = {})
}
