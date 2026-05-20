package com.example.nootepro.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nootepro.databinding.ItemNoteBinding
import com.example.nootepro.domain.models.NoteModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onNoteClick: (NoteModel) -> Unit,
    private val onNoteLongClick: (NoteModel) -> Unit // Naya: Long click ke liye
) : ListAdapter<NoteModel, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteModel) {
            binding.tvTitle.text = note.title
            binding.tvDescription.text = note.description

            // Timestamp ko readable date mein badalna
            val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            binding.tvTimestamp.text = sdf.format(Date(note.timestamp))

            // 1. Simple Click (Note kholne ke liye)
            binding.root.setOnClickListener {
                onNoteClick(note)
            }

            // 2. Long Click (Delete popup dikhane ke liye)
            binding.root.setOnLongClickListener {
                onNoteLongClick(note)
                true // Iska matlab click handle ho gaya, simple click trigger nahi hoga
            }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel) = oldItem == newItem
    }
}