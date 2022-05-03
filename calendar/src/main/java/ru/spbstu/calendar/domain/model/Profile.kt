package ru.spbstu.calendar.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val avatarUrl: String,
    val email: String? = null,
    val phone: String? = null,
)