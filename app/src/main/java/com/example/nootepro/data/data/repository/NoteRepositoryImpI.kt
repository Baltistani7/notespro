package com.example.nootepro.data.repository

import com.example.nootepro.domain.repository.NoteRepository
import com.example.nootepro.domian.models.NoteModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepositoryImpl(
    private val firestore: FirebaseFirestore
) : NoteRepository {

    // Firebase mein folder (collection) ka naam
    private val notesCollection = firestore.collection("notes")

    override suspend fun addNote(note: NoteModel): Result<Boolean> {
        return try {
            // Firebase khud ID generate kare iske liye document() khali chora
            val docRef = notesCollection.document()
            val finalNote = note.copy(id = docRef.id) // Note mein ID set ki

            docRef.set(finalNote).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllNotes(): Result<List<NoteModel>> {
        return try {
            val snapshot = notesCollection.get().await()
            val notes = snapshot.toObjects(NoteModel::class.java)
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: NoteModel): Result<Boolean> {
        return try {
            notesCollection.document(note.id).set(note).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Boolean> {
        return try {
            notesCollection.document(noteId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}