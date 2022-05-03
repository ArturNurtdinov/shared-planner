package ru.spbstu.calendar.settings.groups.edit.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.spbstu.calendar.databinding.AddParticipantLayoutBinding
import ru.spbstu.calendar.databinding.ParticipantItemBinding
import ru.spbstu.common.extensions.setDebounceClickListener

class ParticipantsAdapter(private val clickListener: (ParticipantUi) -> Unit) :
    ListAdapter<ParticipantUi, RecyclerView.ViewHolder>(ParticipantUiDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ParticipantUi.PARTICIPANT_UI_ITEM_VIEW_TYPE -> ParticipantViewHolder(
                ParticipantItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), clickListener
            )
            ParticipantUi.ADD_PARTICIPANT_VIEW_TYPE -> AddParticipantViewHolder(
                AddParticipantLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), clickListener
            )
            else -> throw IllegalStateException("No such view type")
        }
    }

    override fun getItemViewType(position: Int): Int = currentList[position].viewType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = currentList[position]) {
            is ParticipantUi.ParticipantUiItem -> {
                (holder as? ParticipantViewHolder)?.bind(currentItem)
            }
            is ParticipantUi.AddParticipant -> {
                (holder as? AddParticipantViewHolder)?.bind()
            }
        }
    }

    class ParticipantViewHolder(
        val binding: ParticipantItemBinding,
        private val clickListener: (ParticipantUi) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participantUi: ParticipantUi.ParticipantUiItem) {
            binding.participantItemAvatarLayout.root.isVisible =
                participantUi.profile.avatarUrl.isNotEmpty()
            binding.participantItemTitle.text = participantUi.profile.name
            binding.participantItemSubtitle.text = "Creator"
            binding.participantItemRemove.setDebounceClickListener {
                clickListener.invoke(participantUi)
            }
            if (participantUi.profile.avatarUrl.isNotEmpty()) {
                Glide.with(itemView)
                    .load(participantUi.profile.avatarUrl)
                    .centerCrop()
                    .into(binding.participantItemAvatarLayout.layoutAvatarAvatar)
            }
        }
    }

    class AddParticipantViewHolder(
        val binding: AddParticipantLayoutBinding,
        private val clickListener: (ParticipantUi) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            itemView.setDebounceClickListener {
                clickListener.invoke(ParticipantUi.AddParticipant)
            }
        }
    }

    private object ParticipantUiDiffUtil : DiffUtil.ItemCallback<ParticipantUi>() {
        override fun areItemsTheSame(oldItem: ParticipantUi, newItem: ParticipantUi): Boolean {
            return (oldItem is ParticipantUi.ParticipantUiItem && newItem is ParticipantUi.ParticipantUiItem && oldItem.profile.id == newItem.profile.id)
                    || (oldItem is ParticipantUi.AddParticipant && newItem is ParticipantUi.AddParticipant)
        }

        override fun areContentsTheSame(oldItem: ParticipantUi, newItem: ParticipantUi): Boolean {
            return oldItem == newItem
        }

    }
}