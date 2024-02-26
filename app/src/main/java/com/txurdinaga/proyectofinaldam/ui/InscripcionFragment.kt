package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionBinding


class InscripcionFragment : Fragment() {

    private var _binding: FragmentInscripcionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInscripcionBinding.inflate(layoutInflater, container, false)

        navegacion()

        return binding.root
    }

    private fun navegacion() {
        val url1 = "https://fen.org.es/MercadoAlimentosFEN/pdfs/manzana.pdf"
        val url2 = "https://fen.org.es/MercadoAlimentosFEN/pdfs/manzana.pdf"
        val url3 = "https://fen.org.es/MercadoAlimentosFEN/pdfs/manzana.pdf"

        binding.btnPdf1.setOnClickListener {
            Archivos(requireContext()).descargarPDF(url1)
        }

        binding.btnPdf2.setOnClickListener {
            Archivos(requireContext()).descargarPDF(url2)
        }

        binding.btnPdf3.setOnClickListener {
            Archivos(requireContext()).descargarPDF(url3)
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_inscripcionFragment_to_inscripcionDosFragment)
        }
    }
}
