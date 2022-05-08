package ru.spbstu.calendar.settings.groups.edit.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.spbstu.calendar.databinding.ParticipantItemSearchBinding
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.extensions.setDebounceClickListener

class ParticipantSearchAdapter(private val clickListener: (Profile) -> Unit) :
    ListAdapter<Profile, ParticipantSearchAdapter.ParticipantViewHolder>(ParticipantUiDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        return ParticipantViewHolder(
            ParticipantItemSearchBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), clickListener
        )
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ParticipantViewHolder(
        val binding: ParticipantItemSearchBinding,
        private val clickListener: (Profile) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(profile: Profile) {
            itemView.setDebounceClickListener {
                clickListener.invoke(profile)
            }
            binding.participantItemAvatarLayout.root.isVisible =
                !profile.avatarUrl.isNullOrEmpty()
            binding.participantItemTitle.text = profile.name
            if (!profile.avatarUrl.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(profile.avatarUrl)
                    .centerCrop()
                    .into(binding.participantItemAvatarLayout.layoutAvatarAvatar)
            }
        }
    }

    private object ParticipantUiDiffUtil : DiffUtil.ItemCallback<Profile>() {
        override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
            return oldItem == newItem
        }

    }
}