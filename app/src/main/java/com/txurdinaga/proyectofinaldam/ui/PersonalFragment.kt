package com.txurdinaga.proyectofinaldam.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(layoutInflater, container, false)

        binding.btnDialogo.setOnClickListener {
            showCustomDialog()
        }
        return binding.root
    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.card_edit_personal, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogView)

        val btnEditar = dialogView.findViewById<ImageView>(R.id.imageViewEditar)
        val btnCancelar = dialogView.findViewById<ImageView>(R.id.imageViewCancelar)
        val btnConfirmar = dialogView.findViewById<ImageView>(R.id.imageViewConfirmar)



        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
