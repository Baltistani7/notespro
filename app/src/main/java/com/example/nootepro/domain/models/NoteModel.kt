package com.example.nootepro.domain.models

data class NoteModel(
    val id: String = "",           // Ye missing tha, ise add karein
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0L,
    val category: String = "",      // Category ke liye
    val color: String = ""          // Theme color ke liye
)
