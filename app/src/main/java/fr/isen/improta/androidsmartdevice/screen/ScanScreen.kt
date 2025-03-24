package fr.isen.improta.androidsmartdevice.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.improta.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

data class BLEDevice(val name: String, val address: String, val rssi: Int)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen() {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }

    // Simule des appareils BLE pour l'interface
    val devices = remember {
        mutableStateListOf(
            BLEDevice("Device Unknown", "47:28:A3:7C:E2:BE", -84),
            BLEDevice("Labo_IoT", "00:80:E1:26:6F:6E", -61),
            BLEDevice("Device Unknown", "53:C7:E7:5E:D8:2E", -88),
            BLEDevice("Device Unknown", "4A:6C:22:2E:ED:DE", -97)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AndroidSmartDevice") },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            // Titre + bouton play/pause
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isScanning) "Scan BLE en cours ..." else "Lancer le Scan BLE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                IconButton(onClick = {
                    isScanning = !isScanning
                }) {
                    Icon(
                        imageVector = if (isScanning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Scan Icon",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Divider(color = Color.LightGray)

            // Liste des appareils BLE
            LazyColumn {
                items(devices) { device ->
                    BLEDeviceItem(device)
                }
            }
        }
    }
}

@Composable
fun BLEDeviceItem(device: BLEDevice) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFF1976D2),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = device.rssi.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = device.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = device.address,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
