package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.cw.CurrentUser.currentUser
import com.google.android.material.snackbar.Snackbar
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
            showSnackbar("Account created, please login")
        }
        appDatabase = AppDatabase(this)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            val user = appDatabase.getUserByName(username)

            if (user.password.isEmpty()) {
                showSnackbar("Username not found")
                return@setOnClickListener
            }

            if (BCrypt.checkpw(password, user.password)) {
                currentUser = user
                showSnackbar("Logged in as $username")
                val intent = Intent(this, RestaurantListActivity::class.java)
                startActivity(intent)
            } else {
                showSnackbar("Wrong password")
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
