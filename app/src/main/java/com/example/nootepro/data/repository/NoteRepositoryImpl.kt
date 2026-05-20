package com.example.nootepro.data.repository

import com.example.nootepro.domain.models.CategoryModel
import com.example.nootepro.domain.repository.NoteRepository
import com.example.nootepro.domain.models.NoteModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NoteRepository {

    // Helper: User ki email lene ke liye
    private val userEmail: String
        get() = auth.currentUser?.email ?: "anonymous_user"

    // Base Path: users / user_email / categories
    private fun getCategoryCollection() = firestore
        .collection("users")
        .document(userEmail)
        .collection("categories")

    // Path to Notes: users / user_email / categories / category_name / notes
    private fun getNoteCollection(categoryName: String) =
        getCategoryCollection()
            .document(categoryName)
            .collection("notes")

    override suspend fun addCategory(category: CategoryModel): Result<Unit> {
        return try {
            // Category ka naam hi uska Document ID hoga
            getCategoryCollection().document(category.name).set(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addNote(note: NoteModel): Result<Boolean> {
        return try {
            val collection = getNoteCollection(note.category)

            // FIX: Random document() ke bajaye note.title ko ID banayein
            val docRef = collection.document(note.title)

            // Note model mein ID field ko update karein (ID ab title hi hai)
            val finalNote = note.copy(id = docRef.id)

            docRef.set(finalNote).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNotesByCategory(categoryName: String): Result<List<NoteModel>> {
        return try {
            val snapshot = getNoteCollection(categoryName).get().await()
            val notes = snapshot.toObjects(NoteModel::class.java)
            Result.success(notes.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): Result<List<CategoryModel>> {
        return try {
            val snapshot = getCategoryCollection().get().await()
            val categories = snapshot.toObjects(CategoryModel::class.java)
            Result.success(categories.sortedBy { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: NoteModel): Result<Boolean> {
        return try {
            // Update ke waqt bhi wahi title wala document select karein
            getNoteCollection(note.category).document(note.title).set(note).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // FIX: Note Delete karne ke liye categoryName aur noteId dono use kiye hain
    override suspend fun deleteNote(categoryName: String, noteId: String): Result<Boolean> {
        return try {
            getNoteCollection(categoryName).document(noteId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // FIX: Category delete karne ke liye uska naam (ID) use kiya hai
    // NoteRepositoryImpl.kt ke andar deleteCategory function
    override suspend fun deleteCategory(categoryName: String): Result<Boolean> {
        return try {
            // Humne users -> email -> categories -> [CategoryName] path rakha tha
            getCategoryCollection().document(categoryName).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getAllNotes(): Result<List<NoteModel>> {
        // Group queries nested structure mein complex hoti hain
        return Result.success(emptyList())
    }
    // NoteRepositoryImpl.kt ke andar add karein

    override suspend fun renameCategory(oldName: String, newName: String): Result<Boolean> {
        return try {
            val userEmail = auth.currentUser?.email ?: "anonymous"
            val categoryCol = firestore.collection("users").document(userEmail).collection("categories")

            val oldDoc = categoryCol.document(oldName)
            val newDoc = categoryCol.document(newName)

            // 1. Pehle purani category ka data lein
            val categoryData = oldDoc.get().await().toObject(CategoryModel::class.java)

            if (categoryData != null) {
                val batch = firestore.batch()

                // 2. Naya document banayein naye naam ke saath
                batch.set(newDoc, categoryData.copy(name = newName))

                // 3. Purane notes ko naye folder mein copy karein
                val notes = oldDoc.collection("notes").get().await()
                for (noteDoc in notes.documents) {
                    val note = noteDoc.toObject(NoteModel::class.java)
                    note?.let {
                        val newNoteRef = newDoc.collection("notes").document(it.id)
                        batch.set(newNoteRef, it.copy(category = newName)) // Note ki category update karein
                        batch.delete(noteDoc.reference) // Purana note delete
                    }
                }

                // 4. Purani category delete karein
                batch.delete(oldDoc)

                batch.commit().await()
                Result.success(true)
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun renameNote(category: String, oldTitle: String, newTitle: String, note: NoteModel): Result<Boolean> {
        return try {
            val collection = getNoteCollection(category)
            // Naya note banayein naye title ke saath
            val newNote = note.copy(title = newTitle, id = newTitle)

            collection.document(newTitle).set(newNote).await()
            // Purana note delete karein
            collection.document(oldTitle).delete().await()

            Result.success(true)
        } catch (e: Exception) { Result.failure(e) }
    }

}