package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.mindrot.jbcrypt.BCrypt

class Login : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var appDatabase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        val receivedIntent = intent
        val accountNotif = receivedIntent.getBooleanExtra("CREATED", false)
        if (accountNotif) {
            showSnackbar(getString(R.string.account_created))
        }
        appDatabase = AppDatabase(this)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()


            val mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this) {
                task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, RestaurantListActivity::class.java)
                    startActivity(intent)
                } else {
                    showSnackbar(getString(R.string.username_or_password_incorrect))
                }
            }

        }

        val createAccountButton: Button = findViewById(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
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
