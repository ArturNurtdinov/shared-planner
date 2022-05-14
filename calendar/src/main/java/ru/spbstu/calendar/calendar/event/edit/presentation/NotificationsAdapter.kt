package ru.spbstu.calendar.calendar.event.edit.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.R
import ru.spbstu.calendar.databinding.NotificationItemBinding
import ru.spbstu.common.extensions.setDebounceClickListener


class NotificationsAdapter(private val clickListener: (NotificationUi) -> Unit) :
    ListAdapter<NotificationUi, NotificationsAdapter.NotificationViewHolder>(NotificationDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class NotificationViewHolder(
        private val binding: NotificationItemBinding,
        private val clickListener: (NotificationUi) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notificationUi: NotificationUi) {
            when (notificationUi) {
                is NotificationUi.NotificationUiItem -> {
                    binding.notificationItemRemove.isVisible = true
                    itemView.setDebounceClickListener {}
                    binding.notificationItemRemove.setDebounceClickListener {
                        clickListener.invoke(notificationUi)
                    }
                    binding.root.background = ColorDrawable(Color.WHITE)
                    binding.notificationItemTitle.text =
                        notificationUi.notification.name
                    binding.notificationItemTitle.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.black
                        )
                    )
                }
                is NotificationUi.AddNotification -> {
                    itemView.setDebounceClickListener {
                        clickListener.invoke(notificationUi)
                    }
                    binding.notificationItemRemove.isVisible = false
                    binding.root.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.background_selectable_item_list
                    )
                    binding.notificationItemTitle.text =
                        itemView.context.getString(R.string.add_notification)
                    binding.notificationItemTitle.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.primary
                        )
                    )
                }
            }
        }
    }

    private object NotificationDiffUtil : DiffUtil.ItemCallback<NotificationUi>() {
        override fun areItemsTheSame(oldItem: NotificationUi, newItem: NotificationUi): Boolean {
            return (oldItem is NotificationUi.NotificationUiItem && newItem is NotificationUi.NotificationUiItem && oldItem.notification.value == newItem.notification.value)
                    || (oldItem is NotificationUi.AddNotification && newItem is NotificationUi.AddNotification)
        }

        override fun areContentsTheSame(oldItem: NotificationUi, newItem: NotificationUi): Boolean {
            return oldItem == newItem
        }

    }
}