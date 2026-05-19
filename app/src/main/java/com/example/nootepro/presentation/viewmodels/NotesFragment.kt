package com.example.nootepro.presentation.viewmodels

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nootepro.R
import com.example.nootepro.databinding.FragmentNotesBinding
import com.example.nootepro.presentation.adapters.NoteAdapter
import com.example.nootepro.presentation.CategorySelectionFragment
import com.example.nootepro.domain.models.NoteModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var binding: FragmentNotesBinding

    // Koin ke zariye ViewModel lena
    private val viewModel: NoteViewModel by viewModel()

    private lateinit var noteAdapter: NoteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotesBinding.bind(view)

        setupRecyclerView()
        observeViewModel()

        // FAB: Category Selection par jane ke liye
        binding.btnAddNote.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, CategorySelectionFragment())
                .addToBackStack(null)
                .commit()
        }

        // Notes fetch karein
        viewModel.fetchNotes()
    }

    private fun setupRecyclerView() {
        // NoteAdapter ab do cheezein leta hai: Click aur LongClick
        noteAdapter = NoteAdapter(
            onNoteClick = { note ->
                // Simple click par sirf message dikhao (Baad mein Edit screen)
                Toast.makeText(context, "Note: ${note.title}", Toast.LENGTH_SHORT).show()
            },
            onNoteLongClick = { note ->
                // Long press par delete popup dikhayein
                showDeleteDialog(note)
            }
        )

        binding.rvNotes.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = noteAdapter
        }
    }

    // Naya Function: Delete Confirmation Popup
    // NotesFragment.kt mein showDeleteDialog function ko aise update karein

    private fun showDeleteDialog(note: NoteModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Kya aap '${note.title}' ko delete karna chahte hain?")
            .setPositiveButton("Yes") { _, _ ->

                // FIX: note.id ki jagah poora note object pass karein
                viewModel.deleteNote(note)

                Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notesState.collect { result ->
                result?.onSuccess { notesList ->
                    noteAdapter.submitList(notesList)
                }
                result?.onFailure { error ->
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
