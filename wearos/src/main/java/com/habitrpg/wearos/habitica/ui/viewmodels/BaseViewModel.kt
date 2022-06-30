package com.habitrpg.wearos.habitica.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitrpg.wearos.habitica.data.repositories.TaskRepository
import com.habitrpg.wearos.habitica.data.repositories.UserRepository
import com.habitrpg.wearos.habitica.managers.LoadingManager
import com.habitrpg.wearos.habitica.models.DisplayedError
import com.habitrpg.wearos.habitica.util.ErrorPresenter
import com.habitrpg.wearos.habitica.util.ExceptionHandlerBuilder
import kotlinx.coroutines.launch

open class BaseViewModel(
    val userRepository: UserRepository,
    val taskRepository: TaskRepository,
    val exceptionBuilder: ExceptionHandlerBuilder,
    val loadingManager: LoadingManager
): ViewModel(), ErrorPresenter {
    override val errorValues = MutableLiveData<DisplayedError>()

    fun retrieveFullUserData() {
        viewModelScope.launch(exceptionBuilder.userFacing(this)) {
            loadingManager.startLoading()
            val user = userRepository.retrieveUser(true)
            taskRepository.retrieveTasks(user?.tasksOrder, true)
            loadingManager.endLoading()
        }
    }
}