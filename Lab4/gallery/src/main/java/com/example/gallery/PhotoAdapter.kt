package com.example.gallery

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.models.PhotoData

class PhotoAdapter(private val context: Context, private val photoDataList: List<PhotoData>) :
        RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoView: ImageView = itemView.findViewById(R.id.photoImageView)
        val photoTitle: TextView = itemView.findViewById(R.id.photoTitle)
        val photoDesc: TextView = itemView.findViewById(R.id.photoDesc)
        val photoTags: TextView = itemView.findViewById(R.id.photoTags)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.photo_item, parent, false)

        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoData = photoDataList[position]

        holder.photoView.setImageURI(MainActivity.createImageUri(context, photoData.filename))
        holder.photoTitle.text = photoData.title
        holder.photoDesc.text = photoData.description
        holder.photoTags.text = MainActivity.addHashes(photoData.tags)

    }

    override fun getItemCount(): Int = photoDataList.size
}
