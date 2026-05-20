package com.example.nootepro.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nootepro.R
import com.example.nootepro.databinding.FragmentCategorySelectionBinding
import com.example.nootepro.domain.models.CategoryModel
import com.example.nootepro.presentation.viewmodels.NoteViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategorySelectionFragment : Fragment() {

    private var _binding: FragmentCategorySelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModel()
    private var selectedColor: String = "#808080"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategorySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupColorSelection()
        observeCategorySaveStatus()

        binding.btnProceed.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                val newCategory = CategoryModel(
                    name = categoryName,
                    color = selectedColor,
                    timestamp = System.currentTimeMillis()
                )
                // Firestore mein save call karein
                viewModel.addCategory(newCategory)
                binding.btnProceed.isEnabled = false // Disable to prevent multiple clicks
            } else {
                binding.etCategoryName.error = "Category name required"
            }
        }
    }

    private fun setupColorSelection() {
        binding.colorBlue.setOnClickListener { selectedColor = "#33B5E5"; updateUI(selectedColor) }
        binding.colorRed.setOnClickListener { selectedColor = "#FF4444"; updateUI(selectedColor) }
        binding.colorGreen.setOnClickListener { selectedColor = "#99CC00"; updateUI(selectedColor) }
        binding.colorOrange.setOnClickListener { selectedColor = "#FFBB33"; updateUI(selectedColor) }
    }

    private fun observeCategorySaveStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryState.collect { result ->
                result?.onSuccess {
                    Toast.makeText(context, "Category Saved!", Toast.LENGTH_SHORT).show()
                    // SUCCESS: Wapis main screen par jayein
                    parentFragmentManager.popBackStack()
                    viewModel.resetCategoryState()
                }
                result?.onFailure { error ->
                    binding.btnProceed.isEnabled = true
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUI(colorHex: String) {
        binding.btnProceed.setBackgroundColor(Color.parseColor(colorHex))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}