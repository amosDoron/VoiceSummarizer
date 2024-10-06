package com.example.voicesummarizer

import Recorder
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicesummarizer.ui.theme.VoiceSummarizerTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private var recorder: Recorder? = null
    private var permissionToRecordAccepted = false
    private val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request audio recording permission
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        // Initialize the Recorder class
        recorder = Recorder(context = getApplicationContext())

        enableEdgeToEdge()
        setContent {
            VoiceSummarizerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecordingScreen(
                        modifier = Modifier.padding(innerPadding),
                        onRecordStart = { startRecordingIfPermissionGranted() },
                        onRecordStop = { recorder?.stopRecording() }
                    )
                }
            }
        }
    }

    private fun startRecordingIfPermissionGranted() {
        if (permissionToRecordAccepted) {
            recorder?.startRecording()
        } else {
            // Request permission again if not granted
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }
}

@Composable
fun RecordingScreen(
    modifier: Modifier = Modifier,
    onRecordStart: () -> Unit,
    onRecordStop: () -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        // Text indicating whether recording is in progress
        Text(text = if (isRecording) "Recording..." else "Tap the mic to start recording")

        Spacer(modifier = Modifier.height(16.dp))

        // Button to start/stop recording with mic icon
        Button(
            onClick = {
                if (isRecording) {
                    onRecordStop()
                } else {
                    onRecordStart()
                }
                isRecording = !isRecording
            },
            modifier = Modifier.size(64.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Filled.Done else Icons.Filled.Call,
                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordingScreenPreview() {
    VoiceSummarizerTheme {
        RecordingScreen(
            onRecordStart = { /* No-op */ },
            onRecordStop = { /* No-op */ }
        )
    }
}
