package com.thanhtuan.myapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.models.TaskStatus
import com.thanhtuan.myapp.repositories.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    
    private val _taskState = MutableLiveData<TaskState>()
    val taskState: LiveData<TaskState> = _taskState

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _photoUrl = MutableLiveData<String>()
    val photoUrl: LiveData<String> = _photoUrl

    fun loadTasks(userId: String) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                val result = repository.getTasks(userId)
                _tasks.value = result
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                repository.createTask(task)
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create task"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                repository.updateTask(task)
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update task"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                repository.deleteTask(taskId)
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete task"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun evaluateTask(taskId: String, evaluation: String, childUid: String) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                repository.evaluateTask(taskId, evaluation, childUid)
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to evaluate task"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun loadChildTasks(childUid: String) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                val result = repository.getChildTasks(childUid)
                _tasks.value = result
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load child tasks"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun loadParentTasks(parentUid: String) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                val result = repository.getParentTasks(parentUid)
                _tasks.value = result
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load parent tasks"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun uploadPhoto(taskId: String, photoUri: Uri) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                val url = repository.uploadPhoto(taskId, photoUri)
                _photoUrl.value = url
                _taskState.value = TaskState.Success
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to upload photo"
                _taskState.value = TaskState.Error
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            _taskState.value = TaskState.Loading
            try {
                repository.updateTaskStatus(taskId, newStatus)
                // Reload tasks after status update
                loadTasks(_tasks.value?.firstOrNull()?.assignedTo ?: return@launch)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update task status"
                _taskState.value = TaskState.Error
            }
        }
    }

    sealed class TaskState {
        object Loading : TaskState()
        object Success : TaskState()
        object Error : TaskState()
    }
} 