package com.example.cw

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager {

    companion object {
        private var mAuth = FirebaseAuth.getInstance()
        private var taskOutcome: Boolean = false
        fun loginAttempt (email: String, password: String, context: Activity): Boolean {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(context) {
                task ->
                taskOutcome = task.isSuccessful
            }
            return taskOutcome
        }

        fun registerAttempt (email: String, password: String, context: Activity): Boolean {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context) {
                task ->
                taskOutcome = task.isSuccessful
                loginAttempt(email, password, context)
            }
            return taskOutcome
        }

        fun logoutAttempt(context: Activity) {
            mAuth.signOut()
        }
    }


}