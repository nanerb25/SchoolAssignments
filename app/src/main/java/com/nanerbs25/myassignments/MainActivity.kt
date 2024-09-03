package com.nanerbs25.myassignments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nanerbs25.myassignments.ui.theme.MonetBasedTheming
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import com.nanerbs25.datafetcher.AssignmentsRepository
import com.nanerbs25.datafetcher.Assignment
import com.nanerbs25.datafetcher.ApiService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            MonetBasedTheming {
                MainHomeActivity()
            }
        }
    }
}

enum class Pages {
    List, Settings, Detail
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainHomeActivity() {
    val page = remember { mutableStateOf(Pages.List) }
    val selectedAssignment = remember { mutableStateOf<Assignment?>(null) }
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var assignmentsRepository = AssignmentsRepository()
    val showDialog = remember { mutableStateOf(false) }

    PINDialogUpload(context = context, showDialog = showDialog)

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            showPermissionDialog = true
        }
    }

    Scaffold(
        bottomBar = {
            if (page.value != Pages.Detail) {
                NavigationBarHome(currentPage = page.value) { selectedPage ->
                    page.value = selectedPage
                }
            }
        },
        floatingActionButton = {
            if (page.value != Pages.Detail) {
                FloatingActionButton(onClick = { showDialog.value = true }) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List")
                }
            }
        },
        content = {
            AnimatedContent(
                targetState = page.value,
                transitionSpec = {
                    // Define the sliding animations
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally(initialOffsetX = { it }) + fadeIn()).togetherWith(
                            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                        )
                    } else {
                        (slideInHorizontally(initialOffsetX = { -it }) + fadeIn()).togetherWith(
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                        )
                    }.using(
                        SizeTransform(clip = false)
                    )
                }, label = ""
            ) { targetPage ->
                when (targetPage) {
                    Pages.Settings -> SettingsScreen()
                    Pages.List -> HomeScreen(context, AssignmentsRepository()) { assignment ->
                        selectedAssignment.value = assignment
                        page.value = Pages.Detail
                    }
                    Pages.Detail -> {
                        selectedAssignment.value?.let {
                            AssignmentDetail(
                                assignment = it,
                                apiService = assignmentsRepository.apiService,
                                onBackClick = { page.value = Pages.List } // Navigate back to List
                            )
                        }
                    }
                }
            }
        }
    )

    if (showPermissionDialog) {
        PermissionDialog(onDismiss = { showPermissionDialog = false })
    }

    // Status Bar Color
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.background
    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    context: Context,
    assignmentsRepository: AssignmentsRepository,
    onAssignmentClick: (Assignment) -> Unit
) {
    var assignments by remember { mutableStateOf<List<Assignment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var fetchError by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableStateOf(1) }
    val pageSize = 10
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(currentPage) {
        coroutineScope.launch {
            try {
                // Fetch assignments for the current page
                val fetchedAssignments = fetchAssignments(assignmentsRepository, currentPage, pageSize)
                // Update the list with the new assignments, maintaining the existing ones
                assignments = (assignments + fetchedAssignments).distinctBy { it.id }.sortedByDescending { it.id }
                fetchError = null
            } catch (e: Exception) {
                fetchError = "Unable to connect to server, Maybe server is in maintenance"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMoreAssignments() {
        isLoading = true
        currentPage++
    }

    val topAppBarTextSize = 28.sp
    val topAppBarElementColor = MaterialTheme.colorScheme.onPrimary

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Assignments", fontSize = topAppBarTextSize) },
                actions = {
                    IconButton(onClick = {
                        // Refresh the list
                        isLoading = true
                        currentPage = 1
                        coroutineScope.launch {
                            try {
                                assignments = fetchAssignments(assignmentsRepository, currentPage, pageSize)
                                fetchError = null
                            } catch (e: Exception) {
                                fetchError = "Unable to connect to server, Maybe server is in maintenance"
                            } finally {
                                isLoading = false
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = topAppBarElementColor,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (isLoading && assignments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (fetchError != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = fetchError!!, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false, // Ensure new posts appear at the top
                modifier = Modifier.padding(16.dp)
            ) {
                items(assignments) { assignment ->
                    AssignmentItem(
                        assignment = assignment,
                        onClick = { onAssignmentClick(assignment) },
                        apiService = assignmentsRepository.apiService
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (!isLoading) {
                        Button(
                            onClick = { loadMoreAssignments() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Load More")
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun PINDialogUpload(context: Context, showDialog: MutableState<Boolean>) {
    var pin by remember { mutableStateOf("") }
    val correctPin = "1234"  // Replace with your actual validation logic

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Enter PIN") },
            text = {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (pin == correctPin) {
                        val intent = Intent(context, UploadAssignmentActivity::class.java)
                        intent.putExtra("PIN", pin)
                        context.startActivity(intent)
                        showDialog.value = false
                    } else {
                        // Show an error message if the PIN is incorrect
                        Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AssignmentItem(
    assignment: Assignment,
    onClick: () -> Unit,
    apiService: ApiService
) {
    val context = LocalContext.current
    var bitmaps by remember { mutableStateOf<MutableList<Bitmap?>>(MutableList(10) { null }) }
    var isLoading by remember { mutableStateOf(true) }
    var mainBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(assignment) {
        val urls = listOf(
            assignment.imageUrl1,
            assignment.imageUrl2,
            assignment.imageUrl3,
            assignment.imageUrl4,
            assignment.imageUrl5,
            assignment.imageUrl6,
            assignment.imageUrl7,
            assignment.imageUrl8,
            assignment.imageUrl9,
            assignment.imageUrl10
        )

        isLoading = true
        urls.forEachIndexed { index, url ->
            if (url != null) {
                val imageName = url.substringAfterLast("/")
                val cachedFile = File(context.cacheDir, imageName)
                if (cachedFile.exists()) {
                    bitmaps[index] = BitmapFactory.decodeFile(cachedFile.path)
                } else {
                    bitmaps[index] = fetchAndCacheImage(apiService, imageName, context)
                }
            }
        }

        // Assuming the main image is the first one
        mainBitmap = bitmaps.firstOrNull()
        isLoading = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = assignment.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = assignment.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                mainBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Main Assignment Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display up to 3 images in a small box
                if (bitmaps.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val imagesToShow = bitmaps.subList(1, minOf(4, bitmaps.size))
                        imagesToShow.forEach { bitmap ->
                            bitmap?.let {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .border(1.dp, Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Additional Assignment Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                        if (bitmaps.size > 4) {
                            Text(
                                text = "+${bitmaps.size - 3} more",
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(1.dp, Color.Gray)
                                    .background(Color.Gray),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}



suspend fun fetchAndCacheImage(apiService: ApiService, imageName: String, context: Context): Bitmap? {
    val response = apiService.getImage(imageName)

    val imageBytes = response.byteStream()

    val cachedFile = File(context.cacheDir, imageName)
    withContext(Dispatchers.IO) {
        FileOutputStream(cachedFile).use { outputStream ->
            imageBytes.copyTo(outputStream)
        }
    }

    return withContext(Dispatchers.IO) {
        BitmapFactory.decodeStream(FileInputStream(cachedFile))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetail(
    assignment: Assignment,
    onBackClick: () -> Unit,
    apiService: ApiService
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var isLoading by remember { mutableStateOf(true) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(assignment.imageUrl1) {
        if (assignment.imageUrl1 != null) {
            val imageName = assignment.imageUrl1!!.substringAfterLast("/")
            val cachedFile = File(context.cacheDir, imageName)
            if (cachedFile.exists()) {
                cachedBitmap = BitmapFactory.decodeFile(cachedFile.path)
                isLoading = false
            } else {
                bitmap = fetchAndCacheImage(apiService, imageName, context)
                isLoading = false
            }
        }
    }

    // Animation for entering and exiting the screen
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
    ) {
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = { Text(text = assignment.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            isVisible = false
                            onBackClick()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor= MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = assignment.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                val imageToShow = cachedBitmap ?: bitmap
                if (isLoading && imageToShow == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(16.dp),
                        strokeWidth = 2.dp
                    )
                } else if (imageToShow != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        bitmap = imageToShow.asImageBitmap(),
                        contentDescription = "Assignment Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val topAppBarTextSize = 28.sp
    val topAppBarElementColor = MaterialTheme.colorScheme.onPrimary

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Settings", fontSize = topAppBarTextSize) },
                navigationIcon = { /* Add navigation icon here if needed */ },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = topAppBarElementColor,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(text = "Content, Work in progress")
            }
            item {
                CardButton(
                    onClick = {
                        val intent = Intent(context, DisplaySettingsActivity::class.java)
                        context.startActivity(intent)
                    },
                    title = "Display",
                    description = "Adjust your display settings"
                )
                CardButton(
                    onClick = {
                        val intent = Intent(context, AboutActivity::class.java)
                        context.startActivity(intent)
                    },
                    title = "About this app",
                    description = "This is our about page"
                )
            }
        }
    }
}



@Composable
fun CardButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title: String,
    description: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun NavigationBarHome(currentPage: Pages, onPageSelected: (Pages) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        NavigationBarItem(
            selected = currentPage == Pages.List,
            onClick = { onPageSelected(Pages.List) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text("Home")
            }
        )
        NavigationBarItem(
            selected = currentPage == Pages.Settings,
            onClick = { onPageSelected(Pages.Settings) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text("Settings")
            }
        )
    }
}

@SuppressLint("InlinedApi")
@Composable
fun PermissionDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Notification Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Needed") },
        text = { Text("This app needs notification permissions to send notifications.") },
        confirmButton = {
            TextButton(onClick = {
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                onDismiss()
            }) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Deny")
            }
        }
    )
}

@SuppressLint("MissingPermission")
fun sendNotification(context: Context) {
    val notificationId = 1
    val channelId = "default_channel"
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val name = "Default Channel"
    val descriptionText = "This is the default channel for app notifications"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelId, name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("New Assignment Available")
        .setContentText("You have a new assignment.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}

suspend fun fetchAssignments(repository: AssignmentsRepository, page: Int, pageSize: Int): List<Assignment> {
    val start = (page - 1) * pageSize
    return repository.fetchAssignments(start, pageSize)
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MonetBasedTheming {
        MainHomeActivity()
    }
}