package com.thanhtuan.myapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhtuan.myapp.repositories.UserRepository
import kotlinx.coroutines.launch

class LinkAccountViewModel(private val repository: UserRepository) : ViewModel() {
    private val _linkCode = MutableLiveData<String>()
    val linkCode: LiveData<String> = _linkCode

    private val _linkStatus = MutableLiveData<LinkStatus>()
    val linkStatus: LiveData<LinkStatus> = _linkStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun createLinkCode(parentUid: String) {
        viewModelScope.launch {
            _linkStatus.value = LinkStatus.Loading
            repository.createLinkCode(parentUid)
                .onSuccess { code ->
                    _linkCode.value = code
                    _linkStatus.value = LinkStatus.CodeGenerated
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to generate link code"
                    _linkStatus.value = LinkStatus.Error
                }
        }
    }

    fun linkChildToParent(childUid: String, code: String, childName: String?, childAge: Int?) {
        viewModelScope.launch {
            _linkStatus.value = LinkStatus.Loading
            repository.linkChildToParent(childUid, code, childName, childAge)
                .onSuccess { result ->
                    if (result == "success") {
                        _linkStatus.value = LinkStatus.Success
                    } else {
                        _error.value = result
                        _linkStatus.value = LinkStatus.Error
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to link accounts"
                    _linkStatus.value = LinkStatus.Error
                }
        }
    }

    sealed class LinkStatus {
        object Loading : LinkStatus()
        object CodeGenerated : LinkStatus()
        object Success : LinkStatus()
        object Error : LinkStatus()
    }
} 