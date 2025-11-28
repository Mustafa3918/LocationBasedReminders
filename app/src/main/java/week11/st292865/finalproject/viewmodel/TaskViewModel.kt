package week11.st292865.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import week11.st292865.finalproject.data.TaskModel
import week11.st292865.finalproject.repository.TaskRepository

class TaskViewModel (
    private val repo: TaskRepository = TaskRepository()
) : ViewModel() {

    //State
    private val _activeTasks = MutableStateFlow<List<TaskModel>>(emptyList())
    val activeTasks = _activeTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<TaskModel>>(emptyList())
    val completedTasks = _completedTasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    //causing errors on launch, calling firestore to auth user before login screen and crashing
    // will call from UI after auth
//    init {
//        observeActiveTasks()
//        observeCompletedTasks()
//    }

    fun startObservingTasks() {
        observeActiveTasks()
        observeCompletedTasks()
    }

    //observers
    private fun observeActiveTasks() {
        viewModelScope.launch {
            repo.getActiveTasks().collect {result ->
                if (result.isSuccess) {
                    _activeTasks.value = result.getOrDefault(emptyList())
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            }
        }
    }

    //CRUD
    fun addTask(task: TaskModel) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repo.addTask(task)
            _isLoading.value = false

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun updateTask(task: TaskModel) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repo.updateTask(task)
            _isLoading.value = false

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun deleteTask(taskId: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repo.deleteTask(taskId)
            _isLoading.value = false

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun markTaskComplete(taskId: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val result = repo.markTaskComplete(taskId)
            _isLoading.value = false

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun observeCompletedTasks() {
        viewModelScope.launch {
            repo.getCompletedTasks().collect { result ->
                if (result.isSuccess) {
                    _completedTasks.value = result.getOrDefault(emptyList())
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            }
        }
    }

    fun getTaskById(taskId: String): TaskModel? {
        return activeTasks.value.find {it.id == taskId}
    }

}