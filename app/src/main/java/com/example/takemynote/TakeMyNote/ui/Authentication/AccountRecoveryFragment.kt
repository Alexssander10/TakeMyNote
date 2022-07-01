package com.example.takemynote.TakeMyNote.ui.Authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.takemynote.TakeMyNote.helper.BaseFragment
import com.example.takemynote.TakeMyNote.helper.FirebaseHelper
import com.example.takemynote.TakeMyNote.helper.initToolbar
import com.example.takemynote.databinding.FragmentAccountRecoveryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AccountRecoveryFragment : BaseFragment() {

    private var _binding: FragmentAccountRecoveryBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountRecoveryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        auth = Firebase.auth

        clickEvent()
    }

    private fun clickEvent(){

        binding.btnRecovery.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.edtEmailRecovery.text.toString().trim()


        if (email.isNotEmpty()) {

            hideKeyboard()

            binding.progressBarRecovery.isVisible = true

            recoveryAccountUser(email)
        } else {
            Toast.makeText(requireContext(), "Insira seu e-mail", Toast.LENGTH_SHORT).show()

        }
    }

    private fun recoveryAccountUser(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Recuperação iniciada, verifique seu e-mail.", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(
                        requireContext(),
                        FirebaseHelper.validError(task.exception?.message ?: ""),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                binding.progressBarRecovery.isVisible = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}