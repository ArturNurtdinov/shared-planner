package ru.spbstu.calendar.calendar.day.presentation

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.databinding.EventItemBinding
import ru.spbstu.calendar.domain.model.Event
import ru.spbstu.common.extensions.dp
import ru.spbstu.common.extensions.setDebounceClickListener

class EventsAdapter(private val clickListener: (Event) -> Unit) :
    ListAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffUtil) {

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
        fun bind(event: Event) {
            binding.eventItemTime.text = event.startTime.toString()
            binding.eventItemGroup.text = event.group.title
            binding.eventItemTitle.text = event.title
            binding.eventItemColor.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4.dp.toFloat()
                setColor(Color.CYAN)
            }
        }
    }

    private object EventDiffUtil : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}