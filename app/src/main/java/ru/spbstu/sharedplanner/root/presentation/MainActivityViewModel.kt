package ru.spbstu.sharedplanner.root.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.Api
import timber.log.Timber

class MainActivityViewModel(
    rootRouter: RootRouter,
    private val api: Api,
    preferencesRepository: PreferencesRepository
) : ViewModel() {

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag(TAG).e("Fetching FCM registration token failed: ${task.exception}")
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Timber.tag(TAG).d("newToken on start: $token")

        }

        viewModelScope.launch(Dispatchers.IO) {
            val currentUserResponse = api.getUser()
            if (currentUserResponse.isSuccessful) {
                val id = currentUserResponse.body()?.id ?: return@launch
                preferencesRepository.setSelfId(id)
            }
        }
    }

    private val _sendTokenResult = Channel<Boolean>(Channel.BUFFERED)
    val sendTokenResult = _sendTokenResult.receiveAsFlow()

    fun sendToken() {

        viewModelScope.launch(Dispatchers.Default) {
            delay(2000)
            _sendTokenResult.send(true)
        }
    }

    companion object {
        private val TAG = MainActivityViewModel::class.simpleName!!
    }
}