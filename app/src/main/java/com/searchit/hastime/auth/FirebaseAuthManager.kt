package com.searchit.hastime.auth

import android.annotation.SuppressLint
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.searchit.hastime.Room.model.User
import com.searchit.hastime.Util.FirebaseUtils

class FirebaseAuthManager {

    private val auth = FirebaseAuth.getInstance()

    fun signInWithGoogle(account: GoogleSignInAccount, onComplete: (User?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val user = User(
                            uid = it.uid,
                            name = it.displayName ?: "No Name",
                            email = it.email ?: "No Email"
                        )
                        FirebaseUtils.saveUserToDatabase(user) // Log details to Firebase
                        onComplete(user)
                    }
                } else {
                    onComplete(null)
                }
            }
    }
}