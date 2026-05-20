package com.example.nootepro.di

import com.example.nootepro.data.repository.NoteRepositoryImpl
import com.example.nootepro.domain.repository.NoteRepository
import com.example.nootepro.domain.use_cases.*
import com.example.nootepro.presentation.viewmodels.NoteViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // 1. Firebase Instance
    single { FirebaseFirestore.getInstance() }
    single { com.google.firebase.auth.FirebaseAuth.getInstance() }

    // 2. Repository Mapping
    single<NoteRepository> { NoteRepositoryImpl(get(), get()) }

    // 3. Use Cases (Ab total 10 UseCases hain)
    factory { AddNoteUseCase(get()) }
    factory { GetNotesUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { AddCategoryUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { DeleteCategoryUseCase(get()) }
    factory { GetNotesByCategoryUseCase(get()) }
    factory { UpdateNoteUseCase(get()) }

    // Naye UseCases register kiye gaye
    factory { RenameCategoryUseCase(get()) }
    factory { RenameNoteUseCase(get()) }

    // 4. ViewModel (Ab ye 10 dependencies accept karega)
    viewModel {
        NoteViewModel(
            get(), // addNoteUseCase
            get(), // getNotesUseCase
            get(), // deleteNoteUseCase
            get(), // addCategoryUseCase
            get(), // getCategoriesUseCase
            get(), // deleteCategoryUseCase
            get(), // getNotesByCategoryUseCase
            get(), // updateNoteUseCase
            get(), // renameCategoryUseCase (Naya)
            get()  // renameNoteUseCase (Naya)
        )
    }
}