package com.artemklymenko.cards.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentSignInBinding
import com.artemklymenko.cards.notification.NotificationScheduler
import com.artemklymenko.cards.notification.ReminderNotificationWorker
import com.artemklymenko.cards.utils.Constants.TAG_REMINDER_WORKER
import com.artemklymenko.cards.vm.DataStorePreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        val dataStorePreferenceManager = DataStorePreferenceManager.getInstance(container!!.context)
        binding.switchNotification.isChecked = dataStorePreferenceManager.notice

        val notificationScheduler = NotificationScheduler(container.context)
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            dataStorePreferenceManager.notice = isChecked
            if(isChecked){
                val notificationRequest = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                    .addTag(TAG_REMINDER_WORKER)
                    .build()

                WorkManager.getInstance(container.context).enqueue(notificationRequest)
            } else {
                notificationScheduler.cancelAll()
            }

        }

        binding.btnSignUp.setOnClickListener {
            switchToSignUpFragment()
        }
        return binding.root
    }

    private fun switchToSignUpFragment() {
        val signUpFragment = SignUpFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutSignIn, signUpFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}