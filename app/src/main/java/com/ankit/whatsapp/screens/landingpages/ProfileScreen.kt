package com.ankit.whatsapp.screens.landingpages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ankit.whatsapp.viewmodels.FirebaseViewModel

@Composable
fun ProfileScreen(
    viewModel: FirebaseViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val user by viewModel.userData.collectAsState()   // ✅ observe changes
    var isLoading by remember { mutableStateOf(user == null) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    if (isLoading && user == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (user == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("User data not available")
        }
        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile icon with online indicator
        Box(contentAlignment = Alignment.BottomEnd) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (user!!.onlineStatus) Color(0xFF4CAF50) else Color.Gray
                    )
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Display name
        Text(
            text = user!!.displayName.ifEmpty { "No name available" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(6.dp))

        // Email
        Text(
            text = user!!.email.ifEmpty { "No email available" },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Online status text
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (user!!.onlineStatus) Color(0xFF4CAF50) else Color.Gray
                    )
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (user!!.onlineStatus) "Online" else "Offline",
                color = if (user!!.onlineStatus) Color(0xFF4CAF50) else Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(40.dp))

        // Logout button
        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Logout", color = Color.White)
        }
    }
}
