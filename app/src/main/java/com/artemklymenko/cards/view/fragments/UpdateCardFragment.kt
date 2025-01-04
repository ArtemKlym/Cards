package com.artemklymenko.cards.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.artemklymenko.cards.R
import com.artemklymenko.cards.databinding.FragmentUpdateCardBinding
import com.artemklymenko.cards.db.Words
import com.artemklymenko.cards.firestore.model.Response
import com.artemklymenko.cards.utils.Network
import com.artemklymenko.cards.vm.FirestoreViewModel
import com.artemklymenko.cards.vm.LoginViewModel
import com.artemklymenko.cards.vm.WordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM_ID = "wordsId"
private const val ARG_PARAM_SID = "sid"
private const val ARG_PARAM_SOURCE = "sourceCode"
private const val ARG_PARAM_TARGET = "targetCode"

@AndroidEntryPoint
class UpdateCardFragment : Fragment() {

    private var _binding: FragmentUpdateCardBinding? = null
    private val binding get() = _binding!!

    private val wordsViewModel: WordsViewModel by viewModels()
    private val firestoreViewModel: FirestoreViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private var wordsId: Int = 0
    private var sid: String? = null
    private var source: String? = null
    private var target: String? = null
    private var logIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordsId = it.getInt(ARG_PARAM_ID)
            sid = it.getString(ARG_PARAM_SID)
            source = it.getString(ARG_PARAM_SOURCE)
            target = it.getString(ARG_PARAM_TARGET)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logIn = loginViewModel.currentUser != null
        _binding = FragmentUpdateCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            showSelectedCard(wordsId)

            btnUpdate.setOnClickListener {
                val card = Words(
                    wordsId, etUpdateOrigin.text.toString(),
                    etUpdateTranslated.text.toString(),
                    sourceLangCode = source!!,
                    targetLangCode = target!!
                )
                if (logIn && Network.isConnected(requireContext())) {
                    firestoreViewModel.updateCardFromFirestore(
                        loginViewModel.currentUser!!.uid,
                        sid,
                        card
                    )
                    firestoreViewModel.updateResultCard.observe(viewLifecycleOwner) { response ->
                        if (response is Response.Success) {
                            val result = wordsViewModel.updateWords(card.copy(sid = response.data))
                            checkResult(
                                result,
                                requireContext().getString(R.string.updated),
                                requireContext()
                            )
                        }
                    }
                } else {
                    val result = wordsViewModel.updateWords(card)
                    checkResult(
                        result,
                        requireContext().getString(R.string.updated),
                        requireContext()
                    )
                }
            }

            btnDelete.setOnClickListener {
                val card = Words(
                    wordsId, etUpdateOrigin.text.toString(),
                    etUpdateTranslated.text.toString()
                )
                if (logIn && Network.isConnected(requireContext())) {
                    firestoreViewModel.deleteCardFromFirestore(
                        loginViewModel.currentUser!!.uid,
                        sid
                    )
                    firestoreViewModel.deleteCardResult.observe(viewLifecycleOwner) { response ->
                        if (response is Response.Success) {
                            val result = wordsViewModel.deleteWords(card)
                            checkResult(
                                result,
                                requireContext().getString(R.string.deleted),
                                requireContext()
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                response.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    val result = wordsViewModel.deleteWords(card)
                    checkResult(
                        result,
                        requireContext().getString(R.string.deleted),
                        requireContext()
                    )
                }
            }
        }
    }

    companion object{
        @JvmStatic
        fun newInstance() = UpdateCardFragment()
    }

    private fun checkResult(result: Response<Boolean>, action: String, context: Context) {
        if (result is Response.Success) {
            val message = context.getString(R.string.card_has_been) + " $action"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } else {
            binding.apply {
                etUpdateOrigin.error = context.getString(R.string.incorrect_field)
                etUpdateTranslated.error = context.getString(R.string.incorrect_field)
            }
        }
    }

    private fun showSelectedCard(wordsId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val words = wordsViewModel.getWords(wordsId)
            requireActivity().runOnUiThread {
                binding.etUpdateOrigin.setText(words.origin)
                binding.etUpdateTranslated.setText(words.translated)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}