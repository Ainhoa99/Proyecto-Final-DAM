package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentCerrarSesionBinding
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionBinding


class CerrarSesionFragment : Fragment() {

    private var _binding: FragmentCerrarSesionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCerrarSesionBinding.inflate(layoutInflater, container, false)


        return binding.root
    }


}