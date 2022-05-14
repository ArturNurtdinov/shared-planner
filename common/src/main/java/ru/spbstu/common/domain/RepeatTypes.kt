package ru.spbstu.common.domain

enum class RepeatTypes(val value: Int) {
    NONE(0),
    EVERY_DAY(1),
    EVERY_THREE_DAYS(2),
    EVERY_WEEK(3),
    EVERY_MONTH(4),
    EVERY_YEAR(5);

    companion object {
        fun fromInt(value: Int) = RepeatTypes.values().first { it.value == value }
    }
}