package ru.spbstu.sharedplanner.root.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.spbstu.common.network.Api

class MainActivityViewModel(rootRouter: RootRouter, private val api: Api): ViewModel() {

    private val _sendTokenResult = Channel<Boolean>(Channel.BUFFERED)
    val sendTokenResult = _sendTokenResult.receiveAsFlow()

    fun sendToken() {

        viewModelScope.launch(Dispatchers.Default) {
            delay(2000)
            _sendTokenResult.send(true)
        }
    }
}