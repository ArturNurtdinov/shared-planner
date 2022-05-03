package ru.spbstu.common.extensions


fun CharSequence.getInitials(): CharSequence? {
    try {
        var splitted = this.split(" ")
        if (splitted.size >= 2) {
            return "${splitted[0][0]}${splitted[1][0]}"
        }

        splitted = this.split(".")
        if (splitted.size >= 2) {
            return "${splitted[0][0]}${splitted[1][0]}"
        }

        return this[0].toString()
    } catch (e: Exception) {
        return null
    }
}
