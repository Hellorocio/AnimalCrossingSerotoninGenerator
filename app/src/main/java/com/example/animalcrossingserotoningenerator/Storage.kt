package com.example.animalcrossingserotoningenerator

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File

// Store files in firebase storage
class Storage(private val resources: Resources) {
    private val photoStorage: StorageReference

    init {
        // Create a storage reference from our app
        photoStorage = FirebaseStorage.getInstance().reference.child("images")
    }

    fun uploadJpg(localPath: String, uuid: String) {
       // XXX Write me

        var file = Uri.fromFile(File(localPath))
        val riversRef = photoStorage.child("${uuid}")
        var uploadTask = riversRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
        // Check out https://firebase.google.com/docs/storage/android/upload-files
    }
    // Why would anyone need this?  Read the code to find out.
    fun loadFileToTV(localFile: String, textView: TextView) {
        val bitmap = BitmapFactory.decodeFile(localFile)
        if(bitmap!=null){
            val bitmapResized = Bitmap.createScaledBitmap(bitmap, 500, 500, false)
            val drawable = BitmapDrawable(resources, bitmapResized)
        //val drawable = Drawable.createFromPath(currentPhotoPath)
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        }
    }
    fun downloadJpg(uuid: String, textView: TextView): String {
        val imageRef = photoStorage.child(uuid)
        val localFile = File.createTempFile("images", ".jpg")

        imageRef.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            loadFileToTV(localFile.absolutePath, textView)
        }.addOnFailureListener {
            // Handle any errors

        }

        Log.d("XXX", "Here's the absolute path! Woo! ${localFile.absolutePath}}")

        return localFile.absolutePath

        // Check out. https://firebase.google.com/docs/storage/android/download-files

    }
}