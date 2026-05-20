package com.example.nootepro.domain.use_cases

import com.example.nootepro.domain.repository.NoteRepository

class RenameCategoryUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(oldName: String, newName: String) =
        repository.renameCategory(oldName, newName)
}