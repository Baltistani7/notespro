package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.repository.NoteRepository
import com.example.nootepro.domain.models.NoteModel


class AddNoteUseCase(
    private val repository: NoteRepository
) {
    // operator fun invoke ka matlab hai hum is class ko
    // function ki tarah call kar sakte hain
    suspend operator fun invoke(note: NoteModel): Result<Boolean> {

        // --- Business Logic / Rules ---

        if (note.title.isBlank()) {
            return Result.failure(Exception("Title khali nahi ho sakta!"))
        }

        if (note.title.length < 3) {
            return Result.failure(Exception("Title kam az kam 3 characters ka hona chahiye"))
        }

        // Agar saare rules pass ho gaye, toh Repository ko bolo save kare
        return repository.addNote(note)
    }
}