package com.example.nootepro.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nootepro.R
import com.example.nootepro.databinding.FragmentCategoryNotesBinding
import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.presentation.adapters.NoteAdapter
import com.example.nootepro.presentation.viewmodels.NoteViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryNotesFragment : Fragment() {

    private var _binding: FragmentCategoryNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModel()
    private lateinit var noteAdapter: NoteAdapter

    private var categoryName: String = ""
    private var categoryColor: String = "#FFFFFF"

    companion object {
        fun newInstance(name: String, color: String): CategoryNotesFragment {
            val fragment = CategoryNotesFragment()
            val args = Bundle()
            args.putString("category_name", name)
            args.putString("category_color", color)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryName = arguments?.getString("category_name") ?: ""
        categoryColor = arguments?.getString("category_color") ?: "#FFFFFF"

        setupUI()
        setupRecyclerView()
        observeNotes()

        binding.btnAddNote.setOnClickListener {
            val fragment = AddNoteFragment().apply {
                arguments = Bundle().apply {
                    putString("category_name", categoryName)
                    putString("category_color", categoryColor)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        viewModel.fetchNotesByCategory(categoryName)
    }

    private fun setupUI() {
        binding.tvCategoryTitle.text = categoryName
        try {
            binding.viewHeaderBackground.setBackgroundColor(Color.parseColor(categoryColor))
            binding.clRoot.setBackgroundColor(Color.parseColor(categoryColor))
        } catch (e: Exception) {
            binding.viewHeaderBackground.setBackgroundColor(Color.GRAY)
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                val fragment = AddNoteFragment().apply {
                    arguments = Bundle().apply {
                        putString("note_id", note.id)
                        putString("title", note.title)
                        putString("description", note.description)
                        putString("category_name", categoryName)
                        putString("category_color", categoryColor)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onNoteLongClick = { note ->
                // Long press par naya Options Dialog khulega
                showNoteOptionsDialog(note)
            }
        )

        binding.rvNotes.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
        }
    }

    // --- NEW: OPTIONS DIALOG (Rename & Delete) ---
    private fun showNoteOptionsDialog(note: NoteModel) {
        val options = arrayOf("Rename Note", "Delete Note", "Cancel")
        AlertDialog.Builder(requireContext())
            .setTitle("Note Options")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showRenameNoteDialog(note)
                    1 -> showDeleteConfirmDialog(note)
                    2 -> dialog.dismiss()
                }
            }
            .show()
    }

    // --- NEW: RENAME NOTE DIALOG ---
    private fun showRenameNoteDialog(note: NoteModel) {
        val input = EditText(requireContext())
        input.setText(note.title)
        input.setPadding(50, 40, 50, 40)

        AlertDialog.Builder(requireContext())
            .setTitle("Rename Note")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val newTitle = input.text.toString().trim()
                if (newTitle.isNotEmpty() && newTitle != note.title) {
                    viewModel.renameNote(categoryName, note.title, newTitle, note)
                } else if (newTitle.isEmpty()) {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --- UPDATED: DELETE CONFIRMATION ---
    private fun showDeleteConfirmDialog(note: NoteModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteNote(note)
                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeNotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notesState.collect { result ->
                result?.onSuccess { notes ->
                    noteAdapter.submitList(notes)
                }
                result?.onFailure { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}