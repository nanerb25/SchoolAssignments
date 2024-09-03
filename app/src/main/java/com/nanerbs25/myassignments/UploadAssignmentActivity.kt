package com.nanerbs25.myassignments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nanerbs25.myassignments.ui.theme.MonetBasedTheming
import com.nanerbs25.datafetcher.AssignmentsRepository
import java.io.File

class UploadAssignmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonetBasedTheming {
                UploadScreen(onBackClick = { finish() }) // Call finish() to close the activity
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Upload Assignment") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Select Image",
                modifier = Modifier.clickable { launcher.launch("image/*") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Convert imageUri to File and upload
                    val imageFile = imageUri?.let { uriToFile(context, it) }
                    val repository = AssignmentsRepository()
                    suspend {
                        if (imageFile != null) {
                            repository.uploadAssignment(
                                title = title.value,
                                description = description.value,
                                imageFile = imageFile
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Assignment")
            }
        }
    }

    // Status Bar Color
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.background
    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor)
    }
}

// Function to convert Uri to File
fun uriToFile(context: Context, uri: Uri): File? {
    val filePath = when {
        // Check if the URI is a content URI
        "content" == uri.scheme -> {
            getFilePathFromContentUri(context, uri)
        }
        // Check if the URI is a file URI
        "file" == uri.scheme -> {
            uri.path
        }
        else -> {
            null
        }
    }
    return filePath?.let { File(it) }
}

private fun getFilePathFromContentUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex)
        }
    }
    return null
}
