package com.example.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.models.PhotoData

/*
    adapter class to display each of photo from gallery in the recycler

    context - to use main activity functions that request context
    photoDataList - list of all photos in PhotoData class format
    onItemClick - function that sets in photo click listener
 */
class PhotoAdapter(private val context: Context,
                   private val photoDataList: List<PhotoData>,
                   private val onItemClick: (PhotoData) -> Unit) :
        RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: ImageView = itemView.findViewById(R.id.photoImageView)
        val photoTitle: TextView = itemView.findViewById(R.id.photoTitle)
        val photoDesc: TextView = itemView.findViewById(R.id.photoDesc)
        val photoTags: TextView = itemView.findViewById(R.id.photoTags)
    }
    /*
        class that contains all content in each of photo holder

        needed to create adapter on its base
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        // creates view element from layout
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)

        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        // bind data of specific photo to current holder
        val photoData = photoDataList[position]

        // display all uis
        holder.photoView.setImageURI(MainActivity.createImageUri(context, photoData.filename))
        holder.photoTitle.text = photoData.title
        holder.photoDesc.text = photoData.description
        holder.photoTags.text = MainActivity.addHashes(photoData.tags)

        // onclick set class listener arg
        holder.photoView.setOnClickListener {
            onItemClick(photoData)
        }

    }

    override fun getItemCount(): Int = photoDataList.size
}
