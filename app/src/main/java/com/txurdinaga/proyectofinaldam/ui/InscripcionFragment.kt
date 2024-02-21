package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionBinding


class InscripcionFragment : Fragment() {

    private var _binding: FragmentInscripcionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInscripcionBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}