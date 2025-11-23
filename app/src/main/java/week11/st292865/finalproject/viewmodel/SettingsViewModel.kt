package week11.st292865.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import week11.st292865.finalproject.data.UserModel
import week11.st292865.finalproject.repository.SettingsRepository

class SettingsViewModel(
    private val repo: SettingsRepository = SettingsRepository()
) : ViewModel() {

    private val _user = MutableStateFlow(UserModel())
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()


    fun loadUser() {
        val authUser = repo.auth.currentUser
        if (authUser == null) {
            _error.value = "User not logged in"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repo.loadUser()
                if (result.isSuccess) {
                    val loaded = result.getOrNull() ?: UserModel()
                    _user.value = loaded
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun saveSettings(displayName: String, radius: Int) {
        _isLoading.value = true
        _success.value = false
        _error.value = null

        viewModelScope.launch {
            try {
                val result = repo.updateUser(displayName, radius)
                if (result.isSuccess) {
                    _success.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getCurrentUserEmail(): String {
        val authUser = repo.auth.currentUser
        return authUser?.email ?: ""
    }

    fun logout() = repo.logout()

    fun clearSuccess() {
        _success.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
