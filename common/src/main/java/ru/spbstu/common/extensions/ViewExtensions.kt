package ru.spbstu.common.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.spbstu.common.utils.DebounceClickListener
import ru.spbstu.common.utils.DebouncePostHandler

fun View.setDebounceClickListener(
    delay: Long = DebouncePostHandler.DEFAULT_DELAY,
    onClickListener: View.OnClickListener
) {
    setOnClickListener(DebounceClickListener(delay, onClickListener))
}

fun View.showKeyboard(
) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
            InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard(
) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
            InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}
