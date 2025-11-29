package week11.st292865.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import week11.st292865.finalproject.repository.AuthRepository

class AuthViewModel(
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    // Tracks user  already logged in
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    init {
        // Check if Firebase already has a logged-in user
        _isUserLoggedIn.value = authRepo.getCurrentUser() != null
    }

    fun logout() {
        authRepo.logout()
        _isUserLoggedIn.value = false
    }


    // ---------------- LOGIN ----------------
    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _success.value = false

        viewModelScope.launch {
            val result = authRepo.login(email, password)
            _isLoading.value = false

            if (result.isSuccess) {
                _success.value = true
                _isUserLoggedIn.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // ---------------- REGISTER ----------------
    fun register(email: String, password: String, displayName: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _success.value = false

        viewModelScope.launch {
            val result = authRepo.register(email, password, displayName)
            _isLoading.value = false

            if (result.isSuccess) {
                _success.value = true
                _isUserLoggedIn.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // ---------------- FORGOT PASSWORD ----------------
    fun sendReset(email: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _success.value = false

        viewModelScope.launch {
            val result = authRepo.sendReset(email)
            _isLoading.value = false

            if (result.isSuccess) {
                _success.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message
            }
        }
    }

    // ---------------- ERROR HANDLING ----------------
    fun setError(message: String) {
        _errorMessage.value = message
        _isLoading.value = false
        _success.value = false
    }

    // ---------------- STATE RESET ----------------
    fun clearState() {
        _errorMessage.value = null
        _success.value = false
    }
}
