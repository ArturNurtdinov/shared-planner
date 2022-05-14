package ru.spbstu.common.domain

enum class NotificationsTypes(val value: Int) {
    MIN_5(0),
    MIN_10(1),
    MIN_15(2),
    MIN_30(3),
    HOUR(4),
    DAY(5);

    companion object {
        fun fromInt(value: Int): NotificationsTypes =
            NotificationsTypes.values().first { it.value == value }
    }
}