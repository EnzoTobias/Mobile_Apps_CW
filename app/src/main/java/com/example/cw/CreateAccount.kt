package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import org.mindrot.jbcrypt.BCrypt
import com.google.firebase.auth.FirebaseAuth

class CreateAccount : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var createButton: Button
    private lateinit var appDatabase: AppDatabase
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        createButton = findViewById(R.id.loginButton)

        appDatabase = AppDatabase(this)

        createButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.length < 2) {
                showSnackbar(getString(R.string.emailShort))
                return@setOnClickListener
            }

            if (password.length < 6) {
                showSnackbar(getString(R.string.passwordShort))
                return@setOnClickListener
            }

            if (appDatabase.checkUsernameTaken(username)) {
                showSnackbar(getString(R.string.emailUsed))
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this) {
                    task ->
                if (task.isSuccessful) {
                    mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this) {
                            task ->
                        if (task.isSuccessful) {
                            val userID = mAuth.currentUser!!.uid
                            val newUser = User(username, userID, "")
                            appDatabase.addUser(newUser)
                            mAuth.signOut()
                            val intent = Intent(this, Login::class.java)
                            intent.putExtra("CREATED", true)
                            startActivity(intent)
                            showSnackbar(getString(R.string.accountCreated))

                        } else {
                            showSnackbar(getString(R.string.invalid_email_or_in_use))
                        }
                    }
                } else {
                    showSnackbar(getString(R.string.invalid_email_or_in_use))
                }

            }

            }
        val createAccountButton: Button = findViewById(R.id.loginButton2)
        createAccountButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }


    }

    override fun onBackPressed() {
        val intent = Intent(this, RestaurantListActivity::class.java)
        startActivity(intent)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}