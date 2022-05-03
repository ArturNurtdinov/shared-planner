package ru.spbstu.auth

interface AuthRouter {
    fun pop(): Boolean
    fun openMainPage()
}