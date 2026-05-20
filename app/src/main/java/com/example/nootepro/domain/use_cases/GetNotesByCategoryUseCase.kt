package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.models.NoteModel
import com.example.nootepro.domain.repository.NoteRepository

class GetNotesByCategoryUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(categoryName: String): Result<List<NoteModel>> {
        return repository.getNotesByCategory(categoryName)
    }
}