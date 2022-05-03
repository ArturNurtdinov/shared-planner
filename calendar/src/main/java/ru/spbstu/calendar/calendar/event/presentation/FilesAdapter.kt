package ru.spbstu.calendar.calendar.event.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.spbstu.calendar.databinding.AddFileLayoutBinding
import ru.spbstu.calendar.databinding.FileItemBinding
import ru.spbstu.common.extensions.setDebounceClickListener

class FilesAdapter(private val clickListener: (FileUi) -> Unit) :
    ListAdapter<FileUi, RecyclerView.ViewHolder>(FileUiDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FileUi.FILE_UI_ITEM_VIEW_TYPE -> FileViewHolder(
                FileItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            FileUi.ADD_FILE_VIEW_TYPE -> AddFileViewHolder(
                AddFileLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalStateException("No such view type")
        }
    }

    override fun getItemViewType(position: Int): Int = currentList[position].viewType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = currentList[position]) {
            is FileUi.FileUiItem -> {
                (holder as? FileViewHolder)?.bind(currentItem)
            }
            is FileUi.AddFileItem -> {
                (holder as? AddFileViewHolder)?.bind()
            }
        }
        holder.itemView.setDebounceClickListener {
            clickListener.invoke(currentList[position])
        }
    }

    class FileViewHolder(val binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fileUi: FileUi.FileUiItem) {
            binding.itemFileTitle.text = fileUi.file.title
        }
    }

    class AddFileViewHolder(val binding: AddFileLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    private object FileUiDiffUtil : DiffUtil.ItemCallback<FileUi>() {
        override fun areItemsTheSame(oldItem: FileUi, newItem: FileUi): Boolean {
            return (oldItem is FileUi.FileUiItem && newItem is FileUi.FileUiItem && oldItem.file.id == newItem.file.id)
                    || (oldItem is FileUi.AddFileItem && newItem is FileUi.AddFileItem)
        }

        override fun areContentsTheSame(oldItem: FileUi, newItem: FileUi): Boolean {
            return oldItem == newItem
        }

    }
}