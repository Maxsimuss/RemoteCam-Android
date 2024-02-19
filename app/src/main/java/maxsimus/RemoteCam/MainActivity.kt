@file:OptIn(ExperimentalMaterial3Api::class)

package maxsimus.RemoteCam

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


class MainActivity : ComponentActivity() {
    var cameraRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraRunning = isMyServiceRunning(CameraService::class.java)

        setContent {
            PermCheck()
            MainUI()
        }
    }

    fun startCamera() {
        cameraRunning = true;
        startForegroundService(Intent(this, CameraService::class.java))
    }

    fun stopCamera() {
        cameraRunning = false;
        stopService(Intent(this, CameraService::class.java))
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun PermCheck() {
        val cameraPermissionState = rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        )

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            } else {

            }
        }
        if (!cameraPermissionState.allPermissionsGranted) {
            SideEffect {
                launcher.launch(android.Manifest.permission.CAMERA)
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun MainUI() {
// Camera permission state
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row {
                Button(
                    onClick = { startCamera() },
                    Modifier.padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    enabled = !cameraRunning
                ) {
                    Text(text = "Start Service", color = MaterialTheme.colorScheme.onPrimary)
                }
                Button(
                    onClick = { stopCamera() },
                    Modifier.padding(all = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.error,
                        disabledContainerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    enabled = cameraRunning
                ) {
                    Text(text = "Stop Service", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewMessageCard() {
        MainUI();
    }
}