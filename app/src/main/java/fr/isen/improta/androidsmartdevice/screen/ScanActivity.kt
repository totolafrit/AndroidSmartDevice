package fr.isen.improta.androidsmartdevice.screen

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.isen.improta.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ScanActivity : ComponentActivity() {

    private val devices = mutableStateListOf<BLEDevice>()
    private val isScanning = mutableStateOf(false)
    private val remainingTime = mutableStateOf(0)
    private lateinit var scanner: BluetoothLeScanner
    private lateinit var handler: Handler
    private lateinit var scanCallback: ScanCallback

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            launchScanUI()
        } else {
            setContent {
                AndroidSmartDeviceTheme {
                    PermissionDeniedScreen {
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasPermissions(requiredPermissions())) {
            launchScanUI()
        } else {
            permissionLauncher.launch(requiredPermissions())
        }
    }

    private fun requiredPermissions(): Array<String> {
        val basePermissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            basePermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            basePermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            basePermissions.add(Manifest.permission.BLUETOOTH)
            basePermissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        return basePermissions.toTypedArray()
    }

    private fun hasPermissions(perms: Array<String>): Boolean {
        return perms.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun launchScanUI() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non disponible", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Veuillez activer le Bluetooth", Toast.LENGTH_LONG).show()
        }

        scanner = bluetoothAdapter.bluetoothLeScanner
        handler = Handler(Looper.getMainLooper())
        scanCallback = object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val name = device?.name
                val address = result.device.address
                val rssi = result.rssi

                if (!name.isNullOrBlank() && devices.none { it.address == address }) {
                    devices.add(BLEDevice(name, address, rssi))
                    Log.d("BLE", "Device trouvé: $name [$address] - $rssi dBm")
                }
            }
        }

        setContent {
            AndroidSmartDeviceTheme {
                ScanScreen(
                    devices = devices,
                    isScanning = isScanning.value,
                    remainingTime = remainingTime.value,
                    onStartScan = { startScan() },
                    onStopScan = { stopScan() },
                    onBack = {
                        stopScan()
                        finish()
                    }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        Log.d("BLE", "startScan() appelé")
        Toast.makeText(this, "Scan lancé", Toast.LENGTH_SHORT).show()

        if (isScanning.value) return
        devices.clear()
        remainingTime.value = 10
        isScanning.value = true

        scanner.startScan(scanCallback)
        Log.i("BLE", "Scan BLE démarré")

        // Timer pour le compte à rebours
        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                if (isScanning.value && remainingTime.value > 0) {
                    remainingTime.value -= 1
                    Handler(Looper.getMainLooper()).postDelayed(this, 1000)
                }
            }
        })

        handler.postDelayed({ stopScan() }, 10_000)
    }

    private fun stopScan() {
        Log.d("BLE", "stopScan() appelé")

        if (!isScanning.value) return

        try {
            scanner.stopScan(scanCallback)
            Log.i("BLE", "Scan BLE arrêté")
        } catch (e: SecurityException) {
            Log.e("BLE", "Erreur lors de l'arrêt du scan : ${e.message}")
        }

        isScanning.value = false
        remainingTime.value = 0
        handler.removeCallbacksAndMessages(null)
        Toast.makeText(this, "Scan arrêté", Toast.LENGTH_SHORT).show()
    }
}

// UI affichée si permissions refusées
@Composable
fun PermissionDeniedScreen(onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Permissions refusées", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("L'application a besoin des permissions Bluetooth et Localisation pour fonctionner.")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onRetry) {
                Text("Réessayer")
            }
        }
    }
}
