package ru.spbstu.auth.auth.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.auth.AuthRouter

class AuthViewModel(private val authRouter: AuthRouter) : ViewModel() {

    fun onBackClicked(): Boolean = authRouter.pop()

    fun openMainPage() = authRouter.openMainPage()
}