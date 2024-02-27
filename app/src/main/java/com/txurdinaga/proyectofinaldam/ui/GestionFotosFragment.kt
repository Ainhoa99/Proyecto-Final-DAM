package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionFotosBinding
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionBinding


class GestionFotosFragment : Fragment() {

    private var _binding: FragmentGestionFotosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionFotosBinding.inflate(layoutInflater, container, false)


        return binding.root
    }


}