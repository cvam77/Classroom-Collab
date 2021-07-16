package com.example.classcollab

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

//Just a fragment for login functionality
class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        if (auth.getCurrentUser() != null) {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainScreenFragment)
        }

        val email = view.findViewById<TextView>(R.id.username)
        val password = view.findViewById<TextView>(R.id.password)

        val registerButton = view.findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener(View.OnClickListener { view ->

            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        })

        val loginButton = view.findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener(View.OnClickListener {
            when{
                TextUtils.isEmpty(email.text.toString().trim { it <= ' '}) ->{
                    Toast.makeText(context,
                        "Enter an email address",
                        Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(password.text.toString().trim { it <= ' '}) ->{
                    Toast.makeText(context,
                        "Enter a password",
                        Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val email: String = email.text.toString().trim{it <= ' '}
                    val password: String = password.text.toString().trim{it <= ' '}

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->
                            if (task.isSuccessful){

                                Toast.makeText(context, "Successful Login!", Toast.LENGTH_SHORT).show()

                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainScreenFragment)
                            }
                            else {
                                Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        })

    }



}