package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionEquiposBinding


class GestionEquiposFragment : Fragment() {
    private var _binding: FragmentGestionEquiposBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionEquiposBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}