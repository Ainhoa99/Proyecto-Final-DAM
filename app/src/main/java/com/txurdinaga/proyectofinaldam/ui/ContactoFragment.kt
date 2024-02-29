package com.txurdinaga.proyectofinaldam.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentContactoBinding

class ContactoFragment : Fragment() {
    private var _binding: FragmentContactoBinding? = null
    private val binding get() = _binding!!

    private var allFieldsFilled : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactoBinding.inflate(layoutInflater, container, false)

        var boton = binding.buttonEnviar

        boton.setOnClickListener {
            allFieldsFilled = true

            if (binding.mensaje.text.toString().trim().isEmpty()) {
                binding.mensajeLayout.error = getString(R.string.Escribe_un_mensaje)
                binding.mensajeLayout.requestFocus()
                allFieldsFilled = false
            }

            if (binding.asunto.text.toString().trim().isEmpty()) {
                binding.asuntoLayout.error = getString(R.string.Escribe_un_asunto)
                binding.asuntoLayout.requestFocus()
                allFieldsFilled = false
            }

            if (binding.email.text.toString().trim().isEmpty()) {
                binding.emailLayout.error = getString(R.string.Escribe_un_email)
                binding.emailLayout.requestFocus()
                allFieldsFilled = false
            }

            if (allFieldsFilled) {
                //sendEmail()
            }
        }

        return binding.root
    }

    /*private fun sendEmail() {
        val asunto = binding.asunto.text.toString().trim()
        val correo = "2grupotxurdinaga@gmail.com"
        val mensaje = binding.mensaje.text.toString().trim()

        val senderEmail = binding.email.text.toString().trim()

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(correo))
        intent.putExtra(Intent.EXTRA_SUBJECT, asunto)
        intent.putExtra(Intent.EXTRA_TEXT, mensaje)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(senderEmail))

        intent.type = "message/rfc822"

        try {
            startActivity(Intent.createChooser(intent, "Selecciona una aplicación de correo"))
        } catch (ex: android.content.ActivityNotFoundException) {
            // Maneja la excepción si no hay aplicaciones de correo electrónico disponibles
            Toast.makeText(requireContext(), "No se encontró ninguna aplicación de correo electrónico instalada.", Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            // Maneja otras excepciones que puedan ocurrir
            Toast.makeText(requireContext(), "Error al enviar el correo electrónico.", Toast.LENGTH_SHORT).show()
        }
    }*/
}