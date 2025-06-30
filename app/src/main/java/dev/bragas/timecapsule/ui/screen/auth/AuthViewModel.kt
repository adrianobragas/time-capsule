package dev.bragas.timecapsule.ui.screen.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dev.bragas.timecapsule.data.FirebaseService
import dev.bragas.timecapsule.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel : ViewModel() {
    private val _auth = FirebaseService.auth
    private val _db = FirebaseService.db

    private val _email = MutableStateFlow<String>("")
    private val _password = MutableStateFlow<String>("")
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)

    val email: StateFlow<String> = _email.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()
    val authState: StateFlow<AuthState> = _authState
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    val isEmailValid: StateFlow<Boolean> =
        _email.map { it.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(it).matches() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isPasswordValid: StateFlow<Boolean> = _password.map { it.isNotBlank() && it.length >= 6 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        if (_auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
            _currentUser.value = _auth.currentUser
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun signUp(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authState.value = AuthState.Loading
        _auth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = _auth.currentUser
                    currentUser?.let {
                        val user = User(it.uid, it.email!!)
                        _db.child("users").child(it.uid).setValue(user)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onError("Error to save: ${e.message}")
                            }
                    }
                    AuthState.Authenticated
                } else {
                    onError("Erreur d'inscription")
                }
            }
    }

    fun signIn(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _authState.value = AuthState.Loading
        _auth.signInWithEmailAndPassword(_email.value, _password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    _authState.value = AuthState.Unauthenticated
                    onError("Erreur de connexion")
                }
            }
    }

    fun signOut() {
        _auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}