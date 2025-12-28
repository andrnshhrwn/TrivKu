package com.pab.trivku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pab.trivku.data.AuthRepository
import com.pab.trivku.data.models.User
import com.pab.trivku.data.pref.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // event untuk navigasi
    private val _navigationEvent = MutableLiveData<Int?>()
    val navigationEvent: LiveData<Int?> = _navigationEvent

    val loginStatus = MutableLiveData<String>()
    val registStatus = MutableLiveData<String>()
    val checkEmailStatus = MutableLiveData<String>()
    val resetStatus = MutableLiveData<String>()
    val updateStatus = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)

            when {
                user == null -> {
                    loginStatus.value = "Email tidak terdaftar"
                }

                user.password != password -> {
                    loginStatus.value = "Password salah"
                }

                else -> {
                    sessionManager.saveSession(user)
                    loginStatus.value = "Berhasil"
                }
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {

            fun dataUser(): User {
                return User(
                    username = username,
                    email = email,
                    password = password
                )
            }

            val user = repository.addUser(dataUser())

            when {
                user == -1L -> {
                    registStatus.value = "Registrasi Tidak Berhasil"
                }

                else -> {
                    registStatus.value = "Registrasi Berhasil"
                }
            }

        }
    }

    fun forget(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            when (user) {
                null -> {
                    checkEmailStatus.value = "Email Tidak Ditemukan"
                }

                else -> {
                    _navigationEvent.value = user.id
                }
            }
        }
    }

    fun reset(newPass: String, userId: Int) {
        viewModelScope.launch {
            val user = repository.updatePass(newPass, userId)
            when {
                user > 0 -> {
                    resetStatus.value = "Password berhasil diganti"
                }

                else -> {
                    resetStatus.value = "Password gagal diganti"
                }
            }
        }
    }

    fun updateUser(username: String, email: String, userId: Int) {
        viewModelScope.launch {
            val user = repository.updateUser(username, email, userId)
            when {
                user > 0 -> {
                    val userData = repository.getUserByEmail(email)
                    if (userData != null) {
                        sessionManager.updateUser(userData)
                        updateStatus.value = "Data User Berhasil Diganti"
                    }
                }

                else -> {
                    updateStatus.value = "Data User Gagal Di Update"
                }
            }
        }
    }

    fun onNavigated() {
        _navigationEvent.value = null
    }
}