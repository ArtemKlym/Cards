package com.artemklymenko.cards.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityMainBinding
import com.artemklymenko.cards.view.fragments.CardsFragment
import com.artemklymenko.cards.view.fragments.HomeFragment
import com.artemklymenko.cards.view.fragments.SignInFragment
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        binding.bottomNavigationView.selectedItemId = R.id.home
        replaceFragment(HomeFragment())
    }
    private fun setupBottomNavigation() {
        binding.apply {
            bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
            bottomNavigationView.setOnItemSelectedListener {
                val selectedFragment = when (it.itemId) {
                    R.id.home -> HomeFragment()
                    R.id.cards -> CardsFragment()
                    R.id.settings -> SignInFragment()
                    else -> HomeFragment()
                }
                replaceFragment(selectedFragment)
                true
            }
        }
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}