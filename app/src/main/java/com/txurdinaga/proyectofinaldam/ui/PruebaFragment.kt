package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentCalendarioBinding


class PruebaFragment : Fragment() {
    private var _binding: FragmentCalendarioBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarioBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
}