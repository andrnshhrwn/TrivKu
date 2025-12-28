package com.pab.trivku.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.pab.trivku.data.AuthRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.databinding.FragmentProfileBinding
import com.pab.trivku.viewmodel.AuthViewModel
import com.pab.trivku.viewmodel.AuthViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val dao = AppDatabase.getDatabase(requireContext()).userDao()
        val repository = AuthRepository(dao)
        val factory = AuthViewModelFactory(repository, sessionManager)
        authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        loadUserData()

        authViewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                "Data User Berhasil Diganti" -> {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                    loadUserData()
                }

                else -> {
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val userId = sessionManager.getUserId()

            if (username.isNotEmpty() && email.isNotEmpty()) {
                authViewModel.updateUser(username, email, userId)
            } else {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun loadUserData() {
        val username = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()

        binding.tvUsername.text = username
        binding.etUsername.setText(username)
        binding.tvEmail.text = email
        binding.etEmail.setText(email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}