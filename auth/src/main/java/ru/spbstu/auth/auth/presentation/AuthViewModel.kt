package ru.spbstu.auth.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.auth.AuthRepository
import ru.spbstu.auth.AuthRouter
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.errors.ErrorStringsProvider
import ru.spbstu.common.network.SharedPlannerResult
import timber.log.Timber

class AuthViewModel(
    private val authRouter: AuthRouter,
    private val authRepository: AuthRepository,
    private val errorStringsProvider: ErrorStringsProvider,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    init {
        val access = preferencesRepository.token
        val refresh = preferencesRepository.refresh
        if (access.isNotEmpty() && refresh.isNotEmpty()) {
            openMainPage()
        }
    }

    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

    fun onBackClicked(): Boolean = authRouter.pop()

    fun openMainPage() = authRouter.openMainPage()

    fun getLastSignedType(): PreferencesRepository.LastSignedType? =
        preferencesRepository.lastSignedType

    fun authGoogle(code: String, isAutoAuth: Boolean) {
        Timber.tag(TAG).d("Auth google with code = $code")
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.authGoogle(code)
            when (result) {
                is SharedPlannerResult.Success -> {
                    preferencesRepository.setLastSignedType(PreferencesRepository.LastSignedType.GOOGLE)
                    withContext(Dispatchers.Main) {
                        openMainPage()
                    }
                }
                is SharedPlannerResult.Error -> {
                    if (!isAutoAuth) {
                        _errorMessage.send(errorStringsProvider.provideAuthErrorString())
                    } else {
                        _errorMessage.send("")
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = AuthViewModel::class.simpleName!!
    }
}