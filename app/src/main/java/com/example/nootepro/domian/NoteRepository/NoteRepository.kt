package com.example.nootepro.domain.repository // Package sahi kar lein
import com.example.nootepro.domian.models.NoteModel

interface NoteRepository {

    // 1. Note add karne ka function
    suspend fun addNote(note: NoteModel): Result<Boolean>

    // 2. Saare notes mangwane ka function
    suspend fun getAllNotes(): Result<List<NoteModel>>

    // 3. Note ko update karne ka function
    suspend fun updateNote(note: NoteModel): Result<Boolean>

    // 4. Note delete karne ka function
    suspend fun deleteNote(noteId: String): Result<Boolean>
}