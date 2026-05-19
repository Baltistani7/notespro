package com.example.nootepro.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nootepro.R
import com.example.nootepro.databinding.FragmentAddNoteBinding
import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.presentation.viewmodels.NoteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNoteFragment : Fragment(R.layout.fragment_add_note) {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddNoteBinding.bind(view)

        // 1. Arguments se data nikalna
        val noteId = arguments?.getString("note_id")
        val categoryName = arguments?.getString("category_name") ?: "General"
        val categoryColorHex = arguments?.getString("category_color") ?: "#FFFFFF"
        val isEditMode = noteId != null

        // 2. UI Theme apply karna
        try {
            binding.root.setBackgroundColor(Color.parseColor(categoryColorHex))
        } catch (e: Exception) {
            binding.root.setBackgroundColor(Color.WHITE)
        }

        // 3. Agar Edit Mode hai, toh purana data fields mein bhar do
        if (isEditMode) {
            binding.etTitle.setText(arguments?.getString("title"))
            binding.etDescription.setText(arguments?.getString("description"))
            binding.btnSave.text = "Update Note"
        }

        // 4. Save/Update Button Click Logic
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etDescription.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val note = NoteModel(
                    id = noteId ?: "",
                    title = title,
                    description = content,
                    category = categoryName,
                    color = categoryColorHex,
                    timestamp = System.currentTimeMillis()
                )

                if (isEditMode) {
                    viewModel.updateNote(note)
                    Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addNote(note)
                    Toast.makeText(context, "Note Saved!", Toast.LENGTH_SHORT).show()
                }

                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}