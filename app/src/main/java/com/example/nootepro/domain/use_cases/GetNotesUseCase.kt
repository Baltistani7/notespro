package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.domain.repository.NoteRepository

class GetNotesUseCase(private val repository: NoteRepository) {

    // Ye function Repository se saare notes mangwayega
    suspend operator fun invoke(): Result<List<NoteModel>> {
        return repository.getAllNotes()
    }
}