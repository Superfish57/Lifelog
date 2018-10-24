package com.suckow.lifelog.Activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.suckow.lifelog.MainActivity
import com.suckow.lifelog.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.*

class SignInActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth;
    var TAG = "SignInActivity.kt";
    var user: FirebaseUser? = null;
    val RC_SIGN_IN = 123;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_in_button.setOnClickListener {
            signIn()
        }

        mAuth = FirebaseAuth.getInstance()

        user = FirebaseAuth.getInstance().currentUser;

        if(user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }
    }

    fun signIn() {
        val providers = Arrays.asList(
                AuthUI.IdpConfig.GoogleBuilder().build()

        )

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser

                signIn();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Sign in error", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Firebase Sign in error: " + response!!.error!!.localizedMessage)
            }
        }
    }


}
