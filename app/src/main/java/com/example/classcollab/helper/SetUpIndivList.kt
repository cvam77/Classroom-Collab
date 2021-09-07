package com.example.classcollab.helper

import android.graphics.BitmapFactory
import com.example.classcollab.model.CommentModel
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class SetUpIndivList(
    val indivList: LinkedHashMap<String, CommentModel>
) {
    fun getFileFromFirebase(){
        for (comment in indivList.values){
            val fileName = comment.image

            val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
            val localfile = File.createTempFile("tempImage","jpg")

            storageReference.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                comment.bitmap = bitmap
            }
        }


    }
}