package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.domain.repository.NoteRepository

class UpdateNoteUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(note: NoteModel): Result<Boolean> {
        // Yahan aap validation check kar sakte hain ke update karte waqt title khali na ho
        if (note.title.isBlank()) {
            return Result.failure(Exception("Title cannot be empty"))
        }
        return repository.updateNote(note)
    }
}