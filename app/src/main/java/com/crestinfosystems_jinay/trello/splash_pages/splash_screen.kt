package com.crestinfosystems_jinay.trello.splash_pages

import android.content.Intent
import android.os.Bundle

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crestinfosystems_jinay.trello.Screens_Activity.HomePage
import com.crestinfosystems_jinay.trello.R
import com.crestinfosystems_jinay.trello.databinding.ActivitySplashScreenBinding
import com.crestinfosystems_jinay.trello.network.FirestoreDatabase
import com.crestinfosystems_jinay.trello.splash_pages.data.listOfPages
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import me.relex.circleindicator.CircleIndicator

class splash_screen : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN: Int = 1
    lateinit var gso: GoogleSignInOptions
    lateinit var mAuth: FirebaseAuth
    var currentIndex: Int = 0
    var binding: ActivitySplashScreenBinding? = null
    var indicator: CircleIndicator? = null
    var db: FirestoreDatabase = FirestoreDatabase()
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        indicator = binding?.indicator
        indicator?.createIndicators(listOfPages.size, 0);
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        binding?.splashScreenImage?.setImageResource(listOfPages[currentIndex].drawableImage)
        binding?.splashScreenText?.text = listOfPages[currentIndex].description
        binding?.floatingActionButton?.setOnClickListener {
            if (currentIndex < listOfPages.size - 1) {
                currentIndex++;
                indicator?.animatePageSelected(currentIndex)
            }
            if (currentIndex == 3) {
                binding?.floatingActionButton?.visibility = View.GONE
                binding?.googleSignIn?.visibility = View.VISIBLE
            }
            binding?.splashScreenImage?.setImageResource(listOfPages[currentIndex].drawableImage)
            binding?.splashScreenText?.text = listOfPages[currentIndex].description
        }
        binding?.googleSignIn?.setOnClickListener { signIn() }
        mAuth = FirebaseAuth.getInstance()
        createRequest()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        var db = FirestoreDatabase()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = Firebase.auth.currentUser
                    db.readUserAndEntry(user!!.email!!){
                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createRequest() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}