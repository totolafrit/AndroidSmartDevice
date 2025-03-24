package fr.isen.improta.androidsmartdevice.screen

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.isen.improta.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class ScanActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vérifier si le Bluetooth est dispo
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        // Cas 1 : appareil ne supporte pas le Bluetooth
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non disponible sur cet appareil", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cas 2 : Bluetooth est désactivé → afficher un message mais ne pas forcer l'ouverture
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth désactivé. Veuillez l'activer manuellement.", Toast.LENGTH_LONG).show()
        }

        // Cas OK → lancer l'écran Compose
        setContent {
            AndroidSmartDeviceTheme {
                ScanScreen()
            }
        }
    }
}
