package com.example.firebasegooglesigninwithkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var signInButton : SignInButton
    lateinit var signOutButton : Button
    lateinit var wellcomeTextView : TextView
    lateinit var userEmailTextView: TextView

    var RC_SIG_IN : Int = 1

    lateinit var fAuth : FirebaseAuth
    lateinit var gso : GoogleSignInOptions
    lateinit var signInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initGoogleLogin()
        initViews()

        signInButton.setOnClickListener {
            signIn()
        }
        signOutButton.setOnClickListener {

        }
    }

    private fun signIn() {
        val intentSignIn = signInClient.signInIntent
        startActivityForResult(intentSignIn,RC_SIG_IN)
    }

    private fun initViews() {
        signInButton = findViewById(R.id.signInGoogleButton)
        signOutButton = findViewById(R.id.signOutGoogleButton)
        wellcomeTextView = findViewById(R.id.textView_Wellcome)
        userEmailTextView = findViewById(R.id.textView_userEmail)
    }

    private fun initGoogleLogin() {

    }
}