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
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionUsuarioBinding


class GestionUsuarioFragment : Fragment() {

    private var _binding: FragmentGestionUsuarioBinding? = null
    private val binding get() = _binding!!
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                binding.foto.setImageURI(imageUri)
                binding.foto.visibility = View.VISIBLE
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSelectImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(galleryIntent)
        }

        if (binding.foto.drawable == null) {
            binding.foto.visibility = View.GONE
            binding.space.visibility = View.GONE
            binding.btnSelectImage.visibility = View.VISIBLE
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionUsuarioBinding.inflate(layoutInflater, container, false)



        return binding.root
    }


}