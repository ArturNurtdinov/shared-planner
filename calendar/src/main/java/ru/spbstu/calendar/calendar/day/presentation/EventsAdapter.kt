package ru.spbstu.calendar.calendar.day.presentation

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.databinding.EventItemBinding
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.common.R
import ru.spbstu.common.extensions.dp
import ru.spbstu.common.extensions.setDebounceClickListener
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(private val clickListener: (EventModel) -> Unit) :
    ListAdapter<EventModel, EventsAdapter.EventViewHolder>(EventDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            EventItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setDebounceClickListener {
            clickListener.invoke(currentList[position])
        }
    }

    class EventViewHolder(val binding: EventItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("EE, dd MMMM HH:mm", Locale.getDefault())
        fun bind(event: EventModel) {
            if (event.allDay) {
                binding.eventItemTime.text = itemView.context.getString(R.string.all_day)
            } else {
                if (event.from.isBefore(event.to)) {
                    binding.eventItemTime.text =
                        "${
                            dateFormat.format(
                                event.from.toInstant().toEpochMilli()
                            )
                        } - ${dateFormat.format(event.to.toInstant().toEpochMilli())}"
                } else {
                    binding.eventItemTime.text =
                        dateFormat.format(event.from.toInstant().toEpochMilli())
                }
            }
            binding.eventItemGroup.text = event.group.name
            binding.eventItemTitle.text = event.title
            binding.eventItemColor.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4.dp.toFloat()
                setColor(event.group.color)
            }
        }
    }

    private object EventDiffUtil : DiffUtil.ItemCallback<EventModel>() {
        override fun areItemsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventModel, newItem: EventModel): Boolean {
            return oldItem == newItem
        }
    }
}