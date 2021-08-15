package com.example.classcollab

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.classcollab.databinding.FragmentIndividualQuestionBinding
import com.example.classcollab.databinding.FragmentViewIndividualImageBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ViewIndividualImage : Fragment() {

    private lateinit var storageReference: StorageReference
    private lateinit var binding: FragmentViewIndividualImageBinding
    val arguments: ViewIndividualImageArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_individual_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fileName = arguments.fileName
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        binding = FragmentViewIndividualImageBinding.bind(view)

        val localfile = File.createTempFile("tempImage","jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)

            //comment picture
//            holder.commentPic.visibility=View.VISIBLE
//            holder.commentPic.setImageBitmap(bitmap)
//            holder.commentPic.setOnClickListener(View.OnClickListener {
//                val action = IndividualQuestionDirections.actionIndividualQuestionToViewIndividualImage(fileName.toString())
//                Navigation.findNavController(it).navigate(action)
//            })

            binding.ivIndivImage.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
        }

    }
}