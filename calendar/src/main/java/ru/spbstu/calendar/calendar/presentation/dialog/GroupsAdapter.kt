package ru.spbstu.calendar.calendar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.calendar.presentation.dialog.model.GroupSelected
import ru.spbstu.calendar.databinding.DialogGroupItemBinding
import ru.spbstu.common.extensions.setDebounceClickListener

class GroupsAdapter(private val onCheckedChanged: (GroupSelected, Boolean) -> Unit) :
    ListAdapter<GroupSelected, GroupsAdapter.GroupViewHolder>(GroupsDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            DialogGroupItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class GroupViewHolder(val binding: DialogGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: GroupSelected) {
            binding.root.setDebounceClickListener {
                binding.dialogGroupsItemCheckbox.isChecked = !binding.dialogGroupsItemCheckbox.isChecked
            }
            binding.dialogGroupsItemCheckbox.setOnCheckedChangeListener { compoundButton, b ->
                onCheckedChanged.invoke(group, b)
            }
            binding.dialogGroupsItemTitle.text = group.group.title
            binding.dialogGroupsItemCheckbox.isChecked = group.isSelected
        }
    }

    private object GroupsDiffUtil : DiffUtil.ItemCallback<GroupSelected>() {
        override fun areItemsTheSame(oldItem: GroupSelected, newItem: GroupSelected): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupSelected, newItem: GroupSelected): Boolean {
            return oldItem == newItem
        }

    }
}