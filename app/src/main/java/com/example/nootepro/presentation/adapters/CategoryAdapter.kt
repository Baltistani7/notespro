package com.example.nootepro.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nootepro.databinding.ItemCategoryBinding
import com.example.nootepro.domain.models.CategoryModel

class CategoryAdapter(
    private val onCategoryClick: (CategoryModel) -> Unit,
    private val onDeleteClick: (CategoryModel) -> Unit,
    private val onLongClick: (CategoryModel) -> Unit // Rename ke liye
) : ListAdapter<CategoryModel, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryModel) {
            binding.tvCategoryName.text = category.name

            // Color apply karna
            try {
                binding.cardCategory.setCardBackgroundColor(Color.parseColor(category.color))
            } catch (e: Exception) {
                binding.cardCategory.setCardBackgroundColor(Color.GRAY)
            }

            // 1. Click for Notes (Normal Click)
            binding.root.setOnClickListener { onCategoryClick(category) }

            // 2. Click for Delete
            binding.btnDeleteCategory.setOnClickListener { onDeleteClick(category) }

            // 3. FIXED: Long Click for Rename (Ye line add ki gayi hai)
            binding.root.setOnLongClickListener {
                onLongClick(category)
                true // Iska matlab hai click handle ho gaya
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel): Boolean {
            return oldItem == newItem
        }
    }
}