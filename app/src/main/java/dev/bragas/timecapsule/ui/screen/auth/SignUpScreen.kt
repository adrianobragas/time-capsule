package dev.bragas.timecapsule.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val email by signUpViewModel.email.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val isEmailValid by signUpViewModel.isEmailValid.collectAsState()
    val isPasswordValid by signUpViewModel.isPasswordValid.collectAsState()
    var confirmationPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("S'inscrire") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { signUpViewModel.onEmailChange(it) },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { signUpViewModel.onPasswordChange(it) },
                label = { Text("Mot de passe") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmationPassword,
                onValueChange = { confirmationPassword = it },
                label = { Text("Confirmer mot de passe") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Password,
                        contentDescription = "Password Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = {
                    signUpViewModel.signUp(
                        onSuccess = {
                            Toast.makeText(context, "Bien venue!", Toast.LENGTH_SHORT).show()
                            navController.navigate("timeCapsuleList")
                        },
                        onError = { errorMessage ->
                            println("Sign-up failed: $errorMessage")
                        }
                    )
                },
                enabled = isEmailValid && isPasswordValid && password == confirmationPassword,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()

            ) {
                Text(text = "S'inscrire")
            }
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//fun SignUpPreview(navController: NavController, authViewModel: AuthViewModel) {
//    TimeCapsuleTheme {
//        SignUpScreen(navController, authViewModel)
//    }
//}