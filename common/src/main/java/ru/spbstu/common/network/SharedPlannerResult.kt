package ru.spbstu.common.network

sealed class SharedPlannerResult<T> {
    data class Success<T>(val data: T): SharedPlannerResult<T>()
    data class Error<T>(val error: ErrorEntity): SharedPlannerResult<T>()
}

interface ErrorEntity

object UnknownError: ErrorEntity
object EmptyResult