package com.example.nootepro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nootepro.databinding.FragmentLoginAccountBinding // 1. Sahi Import
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginAccountBinding? = null // 2. Sahi Class Name
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // 3. IDs wahi use karein jo XML mein hain
        binding.btnlogin.setOnClickListener { // 'btnlogin' small 'l' ke sath
            performLogin()
        }

        binding.createaccount.setOnClickListener { // 'createaccount' small 'a' ke sath
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CreateAccountFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.signUpProgress.visibility = View.VISIBLE // XML mein ID 'signUpProgress' hai
        binding.btnlogin.text = ""

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.signUpProgress.visibility = View.GONE
                binding.btnlogin.text = "Login"

                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Success!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}