package com.example.nootepro
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nootepro.databinding.FragmentCreatAccountBinding
import com.example.nootepro.presentation.viewmodels.NotesFragment
import com.google.firebase.auth.FirebaseAuth

// 1. AppCompatActivity ki jagah Fragment() use kiya
class CreateAccountFragment : Fragment() {

    // 2. Fragment mein binding declare karne ka professional tarika
    private var _binding: FragmentCreatAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    // 3. Fragment mein layout inflate karne ke liye onCreateView zaroori hai
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 4. Saara logic (Clicks, Firebase) onViewCreated mein likha jata hai
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            performSignUp()
        }

        binding.tvLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    private fun performSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // --- Professional Validations ---

        // 1. Email Validation (Empty check + Format check)
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email address"
            binding.etEmail.requestFocus()
            return
        }

        // 2. Password Validation (Length check)
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return
        } else if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            binding.etPassword.requestFocus()
            return
        }

        // 3. Confirm Password Validation (Match check)
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm your password"
            binding.etConfirmPassword.requestFocus()
            return
        } else if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            binding.etConfirmPassword.requestFocus()
            return
        }

        // Baki ka Firebase logic yahan se shuru hoga...

        // Loading Start
        binding.signUpProgress.visibility = View.VISIBLE
        binding.btnSignUp.text = ""

        // Firebase Auth Logic
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Loading Stop
                binding.signUpProgress.visibility = View.GONE
                binding.btnSignUp.text = "Sign Up"

                if (task.isSuccessful) {
                    // Fragment mein 'this' ki jagah 'requireContext()' ya 'context' use hota hai
                    Toast.makeText(requireContext(), "Account Created Successfully!", Toast.LENGTH_SHORT).show()

                    // Home screen (NotesFragment) par navigate karein
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NotesFragment())
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // 5. Memory leak se bachne ke liye onDestroyView mein binding null karna zaroori hai
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}