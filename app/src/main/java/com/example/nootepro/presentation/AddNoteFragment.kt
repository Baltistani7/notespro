package com.example.nootepro.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nootepro.databinding.FragmentAddNoteBinding
import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.presentation.viewmodels.NoteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModel()

    private var categoryName: String = ""
    private var noteId: String? = null // Agar update ho raha hai toh ID aayegi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Arguments se data nikalna
        categoryName = arguments?.getString("category_name") ?: ""
        noteId = arguments?.getString("note_id")
        val oldTitle = arguments?.getString("title")
        val oldDescription = arguments?.getString("description")

        // Agar edit mode hai toh data fill karein
        if (noteId != null) {
            binding.etNoteTitle.setText(oldTitle)
            binding.etNoteDescription.setText(oldDescription)
        }

        // SAVE BUTTON CLICK
        binding.btnSaveNote.setOnClickListener {
            saveNote()
        }


    }

    private fun saveNote() {
        val title = binding.etNoteTitle.text.toString().trim()
        val description = binding.etNoteDescription.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Note Model taiyar karein
        val note = NoteModel(
            id = noteId ?: title, // Naya note hai toh title hi ID hai
            title = title,
            description = description,
            category = categoryName, // Fragment ko mila hua category name
            timestamp = System.currentTimeMillis()
        )

        // 2. ViewModel ko call karein
        if (noteId == null) {
            // Naya Note Add ho raha hai
            viewModel.addNote(note)
            Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
        } else {
            // Purana Note Update ho raha hai
            viewModel.updateNote(note)
            Toast.makeText(context, "Note Updated", Toast.LENGTH_SHORT).show()
        }

        // 3. Wapas chale jayein
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}