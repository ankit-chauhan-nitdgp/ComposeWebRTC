package com.ankit.whatsapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ankit.whatsapp.data.User
import com.ankit.whatsapp.respository.FirebaseRepository
import com.ankit.whatsapp.utils.SharedPreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: FirebaseRepository
) : ViewModel() {

    private val _userData = MutableStateFlow<User?>(null)
    val userData = _userData.asStateFlow()

    init {
        // Load once when ViewModel is created
        loadCurrentUser()
    }

    fun loadCurrentUser(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val cached = SharedPreferenceHelper.readUserData(context)

            if (cached != null && !forceRefresh) {
                _userData.value = cached
                return@launch
            }

            val fresh = repo.getCurrentUserData()
            _userData.value = fresh
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repo.login(email, password)
            if (ok) {
                _userData.value = SharedPreferenceHelper.readUserData(context)
            }
            onResult(ok)
        }
    }

    fun register(email: String, password: String, name: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repo.register(email, password, name)
            if (ok) {
                _userData.value = SharedPreferenceHelper.readUserData(context)
            }
            onResult(ok)
        }
    }

    fun setUserStatus(status: Boolean) {
        viewModelScope.launch {
            repo.updateUserOnlineStatus(status)
            _userData.value = _userData.value?.copy(onlineStatus = status)
        }
    }

    fun logout() {
        viewModelScope.launch {
            setUserStatus(false)
            SharedPreferenceHelper.clearUserData(context)
            SharedPreferenceHelper.writeBooleanSharedPreference(
                context,
                SharedPreferenceHelper.IS_LOGGED_IN_KEY,
                false
            )
            _userData.value = null
        }
    }
}

