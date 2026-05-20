package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.repository.NoteRepository

class GetCategoriesUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke() = repository.getCategories()
}