package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.repository.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(categoryName: String, noteId: String) =
        repository.deleteNote(categoryName, noteId)
}