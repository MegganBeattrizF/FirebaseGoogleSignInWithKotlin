package com.example.firebasegooglesigninwithkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    lateinit var signInButton : SignInButton
    lateinit var signOutButton : Button
    lateinit var wellcomeTextView : TextView
    lateinit var userEmailTextView: TextView
    lateinit var progressBar: ProgressBar

    var RC_SIG_IN : Int = 1

    lateinit var fAuth : FirebaseAuth
    lateinit var gso : GoogleSignInOptions
    lateinit var signInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        initGoogleLogin()

        signInButton.setOnClickListener {
            signIn()
        }
        signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        fAuth.signOut()
        signInClient.signOut().addOnCompleteListener(this) { task ->
            updateUI(null)
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }

    }

    private fun signIn() {
        val intentSignIn = signInClient.signInIntent
        startActivityForResult(intentSignIn,RC_SIG_IN)
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        signInButton = findViewById(R.id.signInGoogleButton)
        signOutButton = findViewById(R.id.signOutGoogleButton)
        wellcomeTextView = findViewById(R.id.textView_Wellcome)
        userEmailTextView = findViewById(R.id.textView_userEmail)

        progressBar.visibility = View.GONE
        signOutButton.visibility = View.GONE

    }

    private fun initGoogleLogin() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        signInClient = GoogleSignIn.getClient(this,gso)
        fAuth = FirebaseAuth.getInstance()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIG_IN){
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)!!

        //-TODO: Aqui já podemos pegar o idToken (@account.idToken) para mandar via HTTPS para a API de back-end

            firebaseAuthWithGoogle(account.idToken)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        progressBar.visibility = View.VISIBLE
        val credential :AuthCredential = GoogleAuthProvider.getCredential(idToken,null)
        fAuth.signInWithCredential(credential).addOnCompleteListener(this){task ->
            if (task.isSuccessful){
                val user = fAuth.currentUser
                updateUI(user)
            }else{
                Log.w("firebaseLogException","Falha no login com credencial",task.exception)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        progressBar.visibility = View.GONE
        if (user != null){
            Toast.makeText(this,"Bemvindo(a) ${user.displayName}",Toast.LENGTH_LONG).show()
            userEmailTextView.visibility = View.VISIBLE
            userEmailTextView.text = user.email
            wellcomeTextView.text = user.displayName
            wellcomeTextView.visibility = View.VISIBLE
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
        }else{
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
            userEmailTextView.visibility = View.GONE
            wellcomeTextView.visibility = View.GONE
            userEmailTextView.text = ""
            wellcomeTextView.text = ""
        }

    }

    override fun onStart() {
        super.onStart()
        updateUI(fAuth.currentUser)
    }

    override fun onPause() {
        super.onPause()
        updateUI(fAuth.currentUser)
    }
}