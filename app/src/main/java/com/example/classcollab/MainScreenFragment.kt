package com.example.classcollab

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        val signOutBtn = view.findViewById<Button>(R.id.sign_out)
        signOutBtn.setOnClickListener(View.OnClickListener { view ->
            FirebaseAuth.getInstance().signOut()
            Navigation.findNavController(view).navigate(R.id.action_mainScreenFragment_to_loginFragment)
        })

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!.toString()
        val createChannel = view.findViewById<Button>(R.id.create_channel)
        createChannel.setOnClickListener(View.OnClickListener { view ->
            val action = MainScreenFragmentDirections.actionMainScreenFragmentToCreateClassChannelFragment(uid)
            Navigation.findNavController(view).navigate(action)
        })
//        val signOutBtn = view.
//        // Inflate the layout for this fragment
        return view
    }
}