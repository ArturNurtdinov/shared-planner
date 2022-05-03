package ru.spbstu.calendar.calendar.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.spbstu.calendar.R
import ru.spbstu.calendar.calendar.presentation.dialog.model.GroupSelected
import ru.spbstu.calendar.databinding.DialogGroupsBinding

class ShowGroupsDialogFragment : DialogFragment() {

    private val adapter = GroupsAdapter { groupSelected, newValue ->
        currentGroups =
            currentGroups.map { group -> if (group.id == groupSelected.id) group.copy(isSelected = newValue) else group }
        update()
    }

    private var currentGroups: List<GroupSelected> = emptyList()

    private fun update() {
        adapter.submitList(currentGroups)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        currentGroups = if (savedInstanceState != null) {
            (savedInstanceState.get(GROUPS_KEY)!! as Array<GroupSelected>).toList()
        } else {
            requireArguments().get(GROUPS_KEY)!! as List<GroupSelected>
        }
        val content = DialogGroupsBinding.inflate(LayoutInflater.from(requireContext()))
        content.dialogGroupsGroups.adapter = adapter
        update()
        return MaterialAlertDialogBuilder(requireContext(), R.style.ShowGroupsDialogStyle)
            .setTitle(getString(R.string.show_groups))
            .setView(content.root)
            .setNeutralButton(R.string.cancel) { dialog, which ->

            }
            .setNegativeButton(R.string.configure) { dialog, which ->
                parentFragmentManager.setFragmentResult(
                    RESULT_KEY, bundleOf(
                        CONFIGURE_KEY to CONFIGURE_ACTION
                    )
                )
            }
            .setPositiveButton(R.string.done) { dialog, which ->
                parentFragmentManager.setFragmentResult(
                    RESULT_KEY, bundleOf(
                        GROUPS_RESULT_KEY to currentGroups.toTypedArray()
                    )
                )
            }
            .create()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
            it.attributes = it.attributes?.also {
                it.width = WindowManager.LayoutParams.MATCH_PARENT
                it.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(GROUPS_KEY, currentGroups.toTypedArray())
    }

    companion object {
        const val CONFIGURE_KEY = "ru.spbstu.sharedplanner.CONFIGURE_KEY"
        const val CONFIGURE_ACTION = "ru.spbstu.sharedplanner.CONFIGURE_ACTION"
        const val RESULT_KEY = "ru.spbstu.sharedplanner.GROUPS_RESULT_KEY"
        const val GROUPS_RESULT_KEY = "ru.spbstu.sharedplanner.GROUPS_RESULT_KEY"
        private const val GROUPS_KEY = "ru.spbstu.sharedplanner.GROUPS_KEY"
        fun newInstance(groups: ArrayList<GroupSelected>): ShowGroupsDialogFragment {
            return ShowGroupsDialogFragment().apply {
                arguments = bundleOf(
                    GROUPS_KEY to groups
                )
            }
        }
    }

}