package com.example.cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.MaterialToolbar


abstract class BasicActivity : AppCompatActivity() {
    protected lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutID())
        toolbar = findViewById(R.id.generalToolbar)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topbar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchItem.icon = ContextCompat.getDrawable(this, R.drawable.search)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // None
                return true
            }
        })
        return true
    }

    private fun performSearch(query: String?) {
        // Implement your search logic here
        // Use the query to perform the search operation
        if (!query.isNullOrBlank()) {
            // Perform search based on the query
            // For example: display search results in a list or update UI
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                showToast("Home clicked")
                true
            }
            R.id.action_account -> {
                showToast("Account clicked")
                true
            }

            R.id.action_filter -> {
                // Handle filter action here
                true
            }
            R.id.filter_option_1 -> {

                true
            }
            R.id.filter_option_2 -> {
                // Handle filter option 2 selection
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        showToast("Account clicked")
    }




    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    abstract fun getLayoutID(): Int
}