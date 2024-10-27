package com.searchit.hastime.Util


import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.searchit.hastime.Room.model.User

object FirebaseUtils {

    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun saveUserToDatabase( user: User) {
        user.uid.let {
            database.child(it).setValue(user)
                .addOnSuccessListener {
                    Log.d("FirebaseUtils", "User data saved successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseUtils", "Error saving user data", exception)
                }
        }
    }
}