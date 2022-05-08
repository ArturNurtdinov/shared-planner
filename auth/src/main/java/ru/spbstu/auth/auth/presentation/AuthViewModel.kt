package ru.spbstu.auth.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.spbstu.auth.AuthRepository
import ru.spbstu.auth.AuthRouter
import ru.spbstu.common.errors.ErrorStringsProvider
import ru.spbstu.common.network.SharedPlannerResult

class AuthViewModel(
    private val authRouter: AuthRouter,
    private val authRepository: AuthRepository,
    private val errorStringsProvider: ErrorStringsProvider,
) : ViewModel() {

    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

    fun onBackClicked(): Boolean = authRouter.pop()

    fun openMainPage() = authRouter.openMainPage()

    fun authGoogle(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.authGoogle(code)
            when (result) {
                is SharedPlannerResult.Success -> {
                    openMainPage()
                }
                is SharedPlannerResult.Error -> {
                    _errorMessage.send(errorStringsProvider.provideAuthErrorString())
                }
            }
        }
    }
}