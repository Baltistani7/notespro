package com.example.nootepro.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nootepro.domain.models.CategoryModel
import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.domain.use_cases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val addNoteUseCase: AddNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getNotesByCategoryUseCase: GetNotesByCategoryUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    // Naye UseCases yahan add karein
    private val renameCategoryUseCase: RenameCategoryUseCase,
    private val renameNoteUseCase: RenameNoteUseCase
) : ViewModel() {

    private val _notesState = MutableStateFlow<Result<List<NoteModel>>?>(null)
    val notesState: StateFlow<Result<List<NoteModel>>?> = _notesState

    private val _categoryState = MutableStateFlow<Result<Unit>?>(null)
    val categoryState: StateFlow<Result<Unit>?> = _categoryState

    private val _categoriesListState = MutableStateFlow<Result<List<CategoryModel>>?>(null)
    val categoriesListState: StateFlow<Result<List<CategoryModel>>?> = _categoriesListState

    // --- Category Functions ---

    fun fetchCategories() {
        viewModelScope.launch {
            _categoriesListState.value = getCategoriesUseCase()
        }
    }

    fun addCategory(category: CategoryModel) {
        viewModelScope.launch {
            try {
                addCategoryUseCase(category)
                _categoryState.value = Result.success(Unit)
                fetchCategories() // List refresh karein
            } catch (e: Exception) {
                _categoryState.value = Result.failure(e)
            }
        }
    }

    fun deleteCategory(category: CategoryModel) {
        viewModelScope.launch {
            deleteCategoryUseCase(category.name).onSuccess {
                fetchCategories()
            }
        }
    }

    // --- RENAME FUNCTIONS (FIXED) ---

    fun renameCategory(oldName: String, newName: String) {
        viewModelScope.launch {
            renameCategoryUseCase(oldName, newName).onSuccess {
                // SUCCESS: List refresh karein
                fetchCategories()
            }.onFailure { e ->
                // Error handling (Optional: StateFlow mein error bhejein)
            }
        }
    }

    fun renameNote(category: String, oldTitle: String, newTitle: String, note: NoteModel) {
        viewModelScope.launch {
            renameNoteUseCase(category, oldTitle, newTitle, note).onSuccess {
                fetchNotesByCategory(category) // Refresh list
            }
        }
    }

    // --- Note Functions ---

    fun fetchNotes() {
        viewModelScope.launch {
            _notesState.value = getNotesUseCase()
        }
    }

    fun fetchNotesByCategory(categoryName: String) {
        viewModelScope.launch {
            _notesState.value = getNotesByCategoryUseCase(categoryName)
        }
    }

    fun addNote(note: NoteModel) {
        viewModelScope.launch {
            addNoteUseCase(note)
            refreshNotesList(note.category)
        }
    }

    fun updateNote(note: NoteModel) {
        viewModelScope.launch {
            updateNoteUseCase(note).onSuccess {
                refreshNotesList(note.category)
            }
        }
    }

    fun deleteNote(note: NoteModel) {
        viewModelScope.launch {
            // Repository ko Category aur ID (Title) dono bhej rahe hain
            deleteNoteUseCase(note.category, note.id).onSuccess {
                fetchNotesByCategory(note.category) // Refresh list
            }
        }
    }

    private fun refreshNotesList(categoryName: String) {
        if (categoryName.isNotEmpty()) {
            fetchNotesByCategory(categoryName)
        } else {
            fetchNotes()
        }
    }

    fun resetCategoryState() {
        _categoryState.value = null
    }

}