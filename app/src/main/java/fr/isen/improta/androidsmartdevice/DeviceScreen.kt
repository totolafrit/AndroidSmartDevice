package fr.isen.improta.androidsmartdevice.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    name: String,
    address: String,
    rssi: Int,
    onBack: () -> Unit // 👈 pour gérer la flèche retour
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon appareil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Connexion à :", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Adresse : $address", fontSize = 16.sp)
            Text("RSSI : $rssi dBm", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Text("🔧 Interface de connexion à venir...", color = MaterialTheme.colorScheme.primary)
        }
    }
}
