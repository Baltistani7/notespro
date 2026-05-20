package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.domain.repository.NoteRepository

class RenameNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(category: String, oldTitle: String, newTitle: String, note: NoteModel) =
        repository.renameNote(category, oldTitle, newTitle, note)
}