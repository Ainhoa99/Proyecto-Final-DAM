package com.txurdinaga.proyectofinaldam.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.txurdinaga.proyectofinaldam.data.repo.UserRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentLoginBinding
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
import com.txurdinaga.proyectofinaldam.util.LoginError
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepo: UserRepository


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAccess.setOnClickListener {
            lifecycleScope.launch {
                try {

                    EncryptedPrefsUtil.putString("tokenLogin",userRepo.login(binding.email.text.toString(),binding.password.text.toString()))
                    EncryptedPrefsUtil.putBoolean("isAdmin",userRepo.isAdmin())

                } catch(loginE: LoginError) {
                    // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                }

            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        userRepo = UserRepository()
        return binding.root
    }
}