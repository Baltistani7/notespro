package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.repository.NoteRepository

class DeleteCategoryUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(categoryId: String): Result<Boolean> {
        return repository.deleteCategory(categoryId)
    }
}