package com.thanhtuan.myapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thanhtuan.myapp.models.User
import com.thanhtuan.myapp.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _userStatus = MutableLiveData<UserStatus>()
    val userStatus: LiveData<UserStatus> = _userStatus

    private val _linkStatus = MutableLiveData<LinkStatus>()
    val linkStatus: LiveData<LinkStatus> = _linkStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadUser(uid: String) {
        viewModelScope.launch {
            _userStatus.value = UserStatus.Loading
            repository.getUser(uid).fold(
                onSuccess = { user ->
                    _user.value = user
                    _userStatus.value = UserStatus.Success
                },
                onFailure = { error ->
                    _userStatus.value = UserStatus.Error(error.message ?: "Failed to load user")
                }
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _userStatus.value = UserStatus.Loading
            repository.signOut().fold(
                onSuccess = {
                    _user.value = null
                    _userStatus.value = UserStatus.Success
                },
                onFailure = { error ->
                    _userStatus.value = UserStatus.Error(error.message ?: "Failed to sign out")
                }
            )
        }
    }

    fun createLinkCode(parentUid: String) {
        viewModelScope.launch {
            _linkStatus.value = LinkStatus.Loading
            repository.createLinkCode(parentUid)
                .onSuccess { code ->
                    _linkStatus.value = LinkStatus.CodeGenerated(code)
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to generate link code"
                    _linkStatus.value = LinkStatus.Error(e.message?:"Failed to generate link code")
                }
        }
    }

    fun linkChildToParent(childUid: String, code: String, childName: String?, childAge: Int?) {
        viewModelScope.launch {
            _linkStatus.value = LinkStatus.Loading
            Log.d("UserViewModel", "Starting link process for child: $childUid with code: $code")
            repository.linkChildToParent(childUid, code, childName, childAge)
                .onSuccess { result ->
                    Log.d("UserViewModel", "Link result: $result")
                    if (result == "Liên kết thành công") {
                        _linkStatus.value = LinkStatus.Success
                    } else {
                        _error.value = result
                        _linkStatus.value = LinkStatus.Error(result)
                    }
                }
                .onFailure { e ->
                    Log.e("UserViewModel", "Link failed", e)
                    _error.value = e.message ?: "Failed to link accounts"
                    _linkStatus.value = LinkStatus.Error(e.message ?: "Failed to link accounts")
                }
        }
    }

    sealed class UserStatus {
        object Loading : UserStatus()
        object Success : UserStatus()
        data class Error(val message: String) : UserStatus()
    }

    sealed class LinkStatus {
        object Loading : LinkStatus()
        data class CodeGenerated(val code: String) : LinkStatus()
        object Success : LinkStatus()
        data class Error(val message: String) : LinkStatus()
    }
    suspend fun createLinkCodeAndReturn(parentUid: String): String? {
        return repository.createLinkCode(parentUid).getOrNull()
    }



} 