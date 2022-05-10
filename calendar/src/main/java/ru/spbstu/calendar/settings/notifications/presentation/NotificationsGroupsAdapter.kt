package ru.spbstu.calendar.settings.notifications.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.databinding.NotificationLayoutBinding
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.common.extensions.setDebounceClickListener

class NotificationsGroupsAdapter(private val onNewCheckboxValueListener: (Boolean, Group) -> Unit) :
    ListAdapter<Group, NotificationsGroupsAdapter.GroupViewHolder>(GroupDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            NotificationLayoutBinding.inflate(LayoutInflater.from(parent.context)),
            onNewCheckboxValueListener
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class GroupViewHolder(
        private val binding: NotificationLayoutBinding,
        private val onNewCheckboxValueListener: (Boolean, Group) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.layoutNotificationTitle.text = group.name
            binding.layoutNotificationSwitch.isChecked = group.notificationsEnabled
            binding.root.setDebounceClickListener {
                val newValue = !binding.layoutNotificationSwitch.isChecked
                binding.layoutNotificationSwitch.isChecked = newValue
                onNewCheckboxValueListener.invoke(newValue, group)
            }
        }
    }

    private object GroupDiffUtil : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
            return oldItem == newItem
        }

    }
}