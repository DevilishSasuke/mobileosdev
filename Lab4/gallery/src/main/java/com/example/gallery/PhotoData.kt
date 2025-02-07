package com.example.gallery.models

import java.io.Serializable

data class PhotoData (
    val id: Int,
    val filename: String,
    val title: String = "",
    val description: String = "",
    val tags: String = ""
) : Serializable