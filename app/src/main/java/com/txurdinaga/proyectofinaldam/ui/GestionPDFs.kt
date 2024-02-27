package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionPdfsBinding
import com.txurdinaga.proyectofinaldam.databinding.FragmentInformacionBinding


class GestionPDFs : Fragment() {
    private var _binding: FragmentGestionPdfsBinding? = null
    private val binding get() = _binding!!

    private lateinit var boton: Button

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                handleSelectedFile(data)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionPdfsBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pdf1.setOnClickListener {
            subirPDF(binding.pdf1)
        }
        binding.pdf2.setOnClickListener {
            subirPDF(binding.pdf2)
        }
        binding.pdf3.setOnClickListener {
            subirPDF(binding.pdf3)
        }
        binding.pdf4.setOnClickListener {
            subirPDF(binding.pdf4)
        }
        binding.pdf5.setOnClickListener {
            subirPDF(binding.pdf5)
        }
        binding.pdf6.setOnClickListener {
            subirPDF(binding.pdf6)
        }
        binding.pdf7.setOnClickListener {
            subirPDF(binding.pdf7)
        }
        binding.pdf8.setOnClickListener {
            subirPDF(binding.pdf8)
        }
    }

    private fun subirPDF(btnEmpadronamiento: Button) {
        boton = btnEmpadronamiento
        val downloadsDirectoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val uri = Uri.parse(downloadsDirectoryPath)
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
        getContent.launch(intent)
    }

    private fun handleSelectedFile(data: Intent?) {
        val uri: Uri? = data?.data
        uri?.let { uri ->
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    //val nombreArchivo = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                    //Toast.makeText(requireContext(), "$nombreArchivo subido correctamente", Toast.LENGTH_SHORT).show()

                    // Obtener el color definido en colors.xml
                    val colorPersonalizado = ContextCompat.getColor(requireContext(), R.color.C4)

                    boton.setBackgroundColor(colorPersonalizado)
                    boton.setTextColor(Color.WHITE)
                }
            }
        }
    }
}


