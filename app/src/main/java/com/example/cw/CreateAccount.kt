package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import org.mindrot.jbcrypt.BCrypt

class CreateAccount : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var createButton: Button
    private lateinit var appDatabase: AppDatabase

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
                showSnackbar("Username too short")
                return@setOnClickListener
            }

            if (password.length < 5) {
                showSnackbar("Password too short")
                return@setOnClickListener
            }

            if (appDatabase.checkUsernameTaken(username)) {
                showSnackbar("Username taken")
                return@setOnClickListener
            }
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val freeUserID = appDatabase.getFreeUserID()
            val newUser = User(username, freeUserID, "", hashedPassword)

            val isSuccess = appDatabase.addUser(newUser)
            if (isSuccess) {
                val intent = Intent(this, Login::class.java)
                intent.putExtra("CREATED", true)
                startActivity(intent)
                showSnackbar("Account created, please login")
            } else {
                showSnackbar("Error occurred, please try again")
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