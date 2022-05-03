package ru.spbstu.sharedplanner.root.presentation.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.common.network.Api
import ru.spbstu.sharedplanner.navigation.Navigator

class MainViewModel(private val router: Navigator, private val api: Api) : ViewModel() {

    fun navigate() {
        router.goToAuthPage()
    }
}