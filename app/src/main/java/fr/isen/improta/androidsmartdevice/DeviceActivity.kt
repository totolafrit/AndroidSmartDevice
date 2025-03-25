
package fr.isen.improta.androidsmartdevice.screen

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import fr.isen.improta.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme

class DeviceActivity : ComponentActivity() {

    private var gatt: BluetoothGatt? = null
    private var ledChar: BluetoothGattCharacteristic? = null
    private var notifChar: BluetoothGattCharacteristic? = null

    private val ledStates = mutableStateListOf(false, false, false)
    private val isSubscribed = mutableStateOf(false)
    private val counterValue = mutableStateOf(0)
    private val connectionState = mutableStateOf("Appuyez sur le bouton pour vous connecter")

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name") ?: "Appareil inconnu"
        val address = intent.getStringExtra("address") ?: "N/A"
        val rssi = intent.getIntExtra("rssi", 0)

        setContent {
            AndroidSmartDeviceTheme {
                DeviceScreen(
                    name = name,
                    address = address,
                    rssi = rssi,
                    onBack = { finish() },
                    onConnectClick = { connectToDevice(address, name) },
                    connectionStatus = connectionState.value,
                    isConnected = connectionState.value.contains("✅"),
                    ledStates = ledStates,
                    onLedToggle = { toggleLed(it) },
                    isSubscribed = isSubscribed.value,
                    onSubscribeToggle = { toggleNotifications(it) },
                    counter = counterValue.value
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(address: String, name: String) {
        connectionState.value = "Connexion BLE en cours..."
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(address)

        gatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    connectionState.value = "✅ Connecté à $name"
                    gatt.discoverServices()
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    connectionState.value = "❌ Déconnecté"
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val service3 = gatt.services.getOrNull(2)
                val service4 = gatt.services.getOrNull(3)
                ledChar = service3?.characteristics?.getOrNull(0)
                notifChar = service4?.characteristics?.getOrNull(0)
                Log.d("BLE", "LED char = $ledChar, Notif char = $notifChar")
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                if (characteristic == notifChar) {
                    counterValue.value++
                    Log.d("BLE", "Compteur reçu = ${counterValue.value}")
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun toggleLed(index: Int) {
        val char = ledChar ?: return
        val isOn = ledStates[index]
        val newValue = if (isOn) 0x00 else (index + 1)
        char.value = byteArrayOf(newValue.toByte())
        gatt?.writeCharacteristic(char)
        ledStates[index] = !isOn
    }

    @SuppressLint("MissingPermission")
    private fun toggleNotifications(enable: Boolean) {
        val char = notifChar ?: return
        gatt?.setCharacteristicNotification(char, enable)
        isSubscribed.value = enable
    }

    override fun onDestroy() {
        super.onDestroy()
        gatt?.close()
    }
}
