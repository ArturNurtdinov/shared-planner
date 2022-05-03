package ru.spbstu.calendar.settings.groups.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.R
import ru.spbstu.calendar.databinding.CreateGroupLayoutBinding
import ru.spbstu.calendar.databinding.GroupLayoutBinding
import ru.spbstu.calendar.settings.groups.presentation.model.GroupUi
import ru.spbstu.common.extensions.setDebounceClickListener

class GroupsAdapter(private val clickListener: (GroupUi) -> Unit) :
    ListAdapter<GroupUi, RecyclerView.ViewHolder>(GroupUiDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GroupUi.GROUP_UI_ITEM_VIEW_TYPE -> GroupViewHolder(
                GroupLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            GroupUi.CREATE_GROUP_VIEW_TYPE -> CreateGroupViewHolder(
                CreateGroupLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalStateException("No such view type")
        }
    }

    override fun getItemViewType(position: Int): Int = currentList[position].viewType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = currentList[position]) {
            is GroupUi.GroupUiItem -> {
                (holder as? GroupViewHolder)?.bind(currentItem)
            }
            is GroupUi.CreateGroupItem -> {
                (holder as? CreateGroupViewHolder)?.bind()
            }
        }
        holder.itemView.setDebounceClickListener {
            clickListener.invoke(currentList[position])
        }
    }

    class GroupViewHolder(val binding: GroupLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groupUiItem: GroupUi.GroupUiItem) {
            binding.createGroupLayoutTitle.text = groupUiItem.group.title
            binding.createGroupLayoutSubtitle.text =
                binding.root.context.resources.getQuantityString(
                    R.plurals.participants,
                    groupUiItem.group.participants,
                    groupUiItem.group.participants,
                )
        }
    }

    class CreateGroupViewHolder(val binding: CreateGroupLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    private object GroupUiDiffUtil : DiffUtil.ItemCallback<GroupUi>() {
        override fun areItemsTheSame(oldItem: GroupUi, newItem: GroupUi): Boolean {
            return (oldItem is GroupUi.GroupUiItem && newItem is GroupUi.GroupUiItem && oldItem.group.id == newItem.group.id)
                    || (oldItem is GroupUi.CreateGroupItem && newItem is GroupUi.CreateGroupItem)
        }

        override fun areContentsTheSame(oldItem: GroupUi, newItem: GroupUi): Boolean {
            return oldItem == newItem
        }

    }
}