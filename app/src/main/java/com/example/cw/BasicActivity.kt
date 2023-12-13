package com.example.cw

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth


abstract class BasicActivity : AppCompatActivity() {
    protected lateinit var toolbar: MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID())
        toolbar = findViewById(R.id.generalToolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        val reportItem = menu.findItem(R.id.action_report)
        val mAuth = FirebaseAuth.getInstance()
        val currentUserFire = mAuth.currentUser
        val db = AppDatabase(this)
        if (currentUserFire != null) {
            val currentUser = db.getUserById(currentUserFire.uid)
            if (currentUser.userID != "q9j5x2XT3jW3GmkeA8jWq9gmM172" && currentUser.userID != "mdW5Dn24YPW31l0TzqFWZ8HuF0I3") {
                reportItem.isVisible = false
            }

        } else {
            reportItem.isVisible = false
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, RestaurantListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_account -> {
                if (currentUser == null) {
                    val intent = Intent(this, Login::class.java)
                    this.startActivity(intent)
                } else {
                    val intent = Intent(this, Account::class.java)
                    this.startActivity(intent)
                }
                true
            }
            R.id.action_report -> {
                val intent = Intent(this, Account::class.java)
                intent.putExtra("REPORT", true)
                this.startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }





    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    abstract fun getLayoutID(): Int

}