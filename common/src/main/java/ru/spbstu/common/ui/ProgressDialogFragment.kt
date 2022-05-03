package ru.spbstu.common.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.spbstu.common.R

class ProgressDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = ProgressDialog(requireContext(), R.style.ShowGroupsDialogStyle)
        dialog.setTitle(getString(R.string.please_wait))
        dialog.setMessage(getString(R.string.loading_is_in_progress))
        dialog.isIndeterminate = true
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        return dialog
    }
}