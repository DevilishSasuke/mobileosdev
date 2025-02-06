package com.example.gallery.models

data class PhotoData (
    val id: Int,
    val filename: String,
    val title: String = "",
    val description: String = "",
    val tags: MutableList<String> = mutableListOf<String>()
)