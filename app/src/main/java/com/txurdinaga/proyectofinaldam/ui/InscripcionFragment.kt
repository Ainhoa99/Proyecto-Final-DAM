package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionBinding
import java.io.File


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
            Pdf(requireContext()).descargarPDF(url1)
        }

        binding.btnPdf2.setOnClickListener {
            Pdf(requireContext()).descargarPDF(url2)
        }

        binding.btnPdf3.setOnClickListener {
            Pdf(requireContext()).descargarPDF(url3)
        }
    }
}