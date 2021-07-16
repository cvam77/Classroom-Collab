package com.example.classcollab

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//In this fragment, we create a new class channel
class CreateClassChannelFragment : Fragment() {

    val args: CreateClassChannelFragmentArgs by navArgs()

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_create_class_channel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = args.uid
        val classChannel = view?.findViewById<TextView>(R.id.class_channel_number)

        val randNum = rand(100000,1000000)

        if (classChannel != null) {
            classChannel.setText(randNum.toString())
        }



        // **here check randNum is taken or not**

        val channelNameEt = view.findViewById<EditText>(R.id.class_channel_name_et)

        val createChannelButton = view.findViewById<Button>(R.id.create_channel_btn)
        createChannelButton.setOnClickListener(View.OnClickListener { view ->

            when{
                TextUtils.isEmpty(channelNameEt.text.toString().trim { it <= ' '}) ->{
                    Toast.makeText(context,
                            "Enter an email address",
                            Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val channelName = channelNameEt.text.toString()
                    AddChannelToDb(uid, randNum,channelName)
                    Navigation.findNavController(view).navigate(R.id.action_createClassChannelFragment_to_mainScreenFragment)
                }
            }

        })
    }

    //Adding the created channel id to Database
    private fun AddChannelToDb(uid: String, randNum: Int, channelName: String) {
        database = Firebase.database.reference
        database.child("users").child(uid).child("created").child(randNum.toString()).setValue("channel_id")

        database.child("channels").child(randNum.toString()).child("channel_name").setValue(channelName)
        database.child("channels").child(randNum.toString()).child("book").child("chapter 1").child("question 1").setValue("nothing")
        database.child("channels").child(randNum.toString()).child("assignments").child("assignment 1").child("question 1").setValue("nothing")
        database.child("channels").child(randNum.toString()).child("tests").child("test 1").child("question 1").setValue("nothing")
        database.child("channels").child(randNum.toString()).child("misc").child("question 1").setValue("nothing")

    }


    fun rand(start: Int, end: Int): Int {
        return (Math.random() * (end - start + 1)).toInt() + start
    }
}