package com.example.nootepro.domian.models
data class NoteModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
