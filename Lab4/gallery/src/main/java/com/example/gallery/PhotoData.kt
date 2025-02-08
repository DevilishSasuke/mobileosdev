package com.example.gallery.models

import java.io.Serializable

/*
    class to storage all photo data in one object
 */
data class PhotoData (
    val id: Int,
    val filename: String,
    val title: String = "",
    val description: String = "",
    val tags: String = ""
) : Serializable // to transmit object through "putExtra" in intent calls