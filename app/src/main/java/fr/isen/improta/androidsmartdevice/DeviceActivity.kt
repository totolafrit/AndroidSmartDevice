package fr.isen.improta.androidsmartdevice.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.isen.improta.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class DeviceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération des infos passées depuis ScanActivity
        val name = intent.getStringExtra("name") ?: "Appareil inconnu"
        val address = intent.getStringExtra("address") ?: "N/A"
        val rssi = intent.getIntExtra("rssi", 0)

        // Affichage de l'UI déléguée dans DeviceScreen.kt
        setContent {
            AndroidSmartDeviceTheme {
                DeviceScreen(
                    name = name,
                    address = address,
                    rssi = rssi,
                    onBack = { finish() } // 👈 permet de revenir à ScanActivity
                )
            }
        }
    }
}
