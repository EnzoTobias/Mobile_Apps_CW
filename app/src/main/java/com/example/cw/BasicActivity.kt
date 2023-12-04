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


abstract class BasicActivity : AppCompatActivity() {
    protected lateinit var toolbar: MaterialToolbar

    val locationPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedLocation = data?.data
            // Process the selected location here
        }
    }
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
        if (CurrentUser.currentUser == null || CurrentUser.currentUser!!.userID != 999) {
            reportItem.isVisible = false
        }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, RestaurantListActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_account -> {
                if (CurrentUser.currentUser == null) {
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