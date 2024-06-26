package com.artemklymenko.cards.view.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.ActivityMainBinding
import com.artemklymenko.cards.db.WordsRepositoryDb
import com.artemklymenko.cards.firestore.repository.CardsRepository
import com.artemklymenko.cards.notification.NotificationScheduler
import com.artemklymenko.cards.sync.SyncManager
import com.artemklymenko.cards.utils.Network
import com.artemklymenko.cards.view.fragments.CardsFragment
import com.artemklymenko.cards.view.fragments.HomeFragment
import com.artemklymenko.cards.view.fragments.SettingsFragment
import com.artemklymenko.cards.view.fragments.SignInFragment
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import com.artemklymenko.cards.vm.LoginViewModel
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var logIn = false
    private val loginViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var firestore: CardsRepository
    @Inject
    lateinit var wordsRepository: WordsRepositoryDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        binding.bottomNavigationView.selectedItemId = R.id.home
        replaceFragment(HomeFragment())

        val dataStorePreference = DataStorePreferenceManager.getInstance(this)
        if (dataStorePreference.notice) {
            NotificationScheduler(this).scheduleReminderNotification()
        }

        logIn = loginViewModel.currentUser != null
        if (logIn && Network.isConnected(this)) {
            synchronizeUserData()
        }
    }

    private fun synchronizeUserData() {
        lifecycleScope.launch {
            try {
                val syncManager = SyncManager(firestore, wordsRepository, this@MainActivity)
                syncManager.synchronizeData(requireNotNull(loginViewModel.currentUser).uid)
            } catch (e: Exception) {
                handleSynchronizationError(e)
            }
        }
    }

    private fun handleSynchronizationError(e: Exception) {
        showToast(getString(R.string.failed_to_synchronize_data))
        Log.e("Firestore", "Failed to synchronize data: ${e.message}")
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
        binding.apply {
            bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
            bottomNavigationView.setOnItemSelectedListener {
                val selectedFragment = when (it.itemId) {
                    R.id.home -> HomeFragment()
                    R.id.cards -> CardsFragment()
                    R.id.settings -> if (logIn) {
                        SettingsFragment()
                    } else {
                        SignInFragment()
                    }

                    else -> HomeFragment()
                }
                replaceFragment(selectedFragment)
                true
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}