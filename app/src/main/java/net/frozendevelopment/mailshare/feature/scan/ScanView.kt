package net.frozendevelopment.mailshare.feature.scan

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ContactMail
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import net.frozendevelopment.mailshare.feature.scan.ui.ScanCard
import net.frozendevelopment.mailshare.ui.theme.MailShareTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScanView(
    modifier: Modifier = Modifier,
    state: ScanState,
    onCloseClicked: () -> Unit,
    openSettingsClicked: () -> Unit,
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (!isGranted) {
            // TODO: Handle permission denied
        }
    }

    // Obtain the current context and lifecycle owner
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Remember a LifecycleCameraController for this composable
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            // Bind the LifecycleCameraController to the lifecycleOwner
            bindToLifecycle(lifecycleOwner)
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted || cameraPermissionState.status.shouldShowRationale) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // if camera permissions are denied, show a card with instructions on how to grant them
        // by opening the settings page. Include a button to deeplink to the page to grant them.
        if (cameraPermissionState.status is PermissionStatus.Denied) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Camera Permissions",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Text(
                        text = "Mail Share requires access to your camera to digitize and import your letters. Please grant access in your device settings",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                    )

                    OutlinedButton(onClick = openSettingsClicked) {
                        Text(text = "Open Settings")
                    }
                }
            }
        }

        if (cameraPermissionState.status.isGranted) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    // Initialize the PreviewView and configure it
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_START
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        controller = cameraController // Set the controller to manage the camera lifecycle
                    }
                },
                onRelease = {
                    // Release the camera controller when the composable is removed from the screen
                    cameraController.unbind()
                }
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.TopStart),
            onClick = onCloseClicked
        ) {
            Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(80.dp)
                .align(Alignment.BottomCenter)
                .shadow(10.dp, shape = CircleShape)
                .size(80.dp)
                .background(color = Color.White, shape = CircleShape)
                .clickable {  }
        ) {
            Box(modifier = Modifier
                .fillMaxSize(.9f)
                .background(color = Color.Transparent, shape = CircleShape)
                .border(width = 2.dp, color = Color.Black, shape = CircleShape))
        }
    }
}

@Composable
private fun ScanViewPreview(darkTheme: Boolean, state: ScanState) {
    MailShareTheme(darkTheme) {
        Surface {
            ScanView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onCloseClicked = {},
                openSettingsClicked = {},
            )
        }
    }
}

@Preview
@Composable
private fun ScanViewPreviewLight() {
    ScanViewPreview(
        darkTheme = false,
        state = ScanState()
    )
}

@Preview
@Composable
private fun ScanViewPreviewDark() {
    ScanViewPreview(
        darkTheme = true,
        state = ScanState()
    )
}