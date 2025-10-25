package com.ankit.whatsapp.screens.authpages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ankit.whatsapp.viewmodels.FirebaseViewModel

@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit
) {

    val viewModel : FirebaseViewModel = hiltViewModel<FirebaseViewModel>()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })

        Button(onClick = {
            viewModel.register(email, password, name) {
                if (it) {
                    Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()
                    onRegistrationSuccess
                }
            }
        }) {
            Text("Register")
        }
    }
}
