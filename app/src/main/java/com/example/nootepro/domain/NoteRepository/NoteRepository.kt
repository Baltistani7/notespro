package com.example.nootepro.domain.repository

import com.example.nootepro.domain.models.CategoryModel
import com.example.nootepro.domain.models.NoteModel

interface NoteRepository {
    suspend fun addNote(note: NoteModel): Result<Boolean>
    suspend fun getAllNotes(): Result<List<NoteModel>>
    suspend fun getNotesByCategory(categoryName: String): Result<List<NoteModel>>
    suspend fun updateNote(note: NoteModel): Result<Boolean>
    suspend fun deleteNote(categoryName: String, noteId: String): Result<Boolean>
    // Category Methods
    suspend fun addCategory(category: CategoryModel): Result<Unit>
    suspend fun getCategories(): Result<List<CategoryModel>>
    suspend fun deleteCategory(categoryName: String): Result<Boolean>
    suspend fun renameCategory(oldName: String, newName: String): Result<Boolean>
    suspend fun renameNote(category: String, oldTitle: String, newTitle: String, note: NoteModel): Result<Boolean>}