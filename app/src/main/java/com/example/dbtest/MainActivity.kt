package com.example.dbtest

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        // Same fix as ITBuilding's back button: at the start destination (the user list)
        // there's nothing left to pop, so the default back button would finish this
        // activity - the only one left in the task since LoginActivity already finished
        // itself after login - killing the whole app and forcing a fresh login next time.
        // Backgrounding instead (like pressing Home) keeps the session alive.
        onBackPressedDispatcher.addCallback(this) {
            if (!navController.popBackStack()) {
                moveTaskToBack(true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}