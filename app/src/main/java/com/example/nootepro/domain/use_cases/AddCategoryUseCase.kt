package com.example.nootepro.domain.use_cases
import com.example.nootepro.domain.models.CategoryModel
import com.example.nootepro.domain.repository.NoteRepository

class AddCategoryUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(category: CategoryModel): Result<Unit> {
        return repository.addCategory(category)
    }
}