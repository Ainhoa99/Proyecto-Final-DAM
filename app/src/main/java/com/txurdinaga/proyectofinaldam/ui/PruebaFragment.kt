package com.txurdinaga.proyectofinaldam.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentCalendarioBinding
import com.txurdinaga.proyectofinaldam.databinding.FragmentPruebaBinding
import java.util.Calendar


class PruebaFragment : Fragment() {
    private var _binding: FragmentPruebaBinding? = null
    private val binding get() = _binding!!

    private lateinit var textViewDate: TextView
    private lateinit var buttonDatePicker: Button
    private val calendar: Calendar = Calendar.getInstance()
    private var allFieldsFilled : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPruebaBinding.inflate(layoutInflater, container, false)

        textViewDate = binding.textInputEditTextDate

        textViewDate.setOnClickListener {
            showDatePicker()
        }

        var boton = binding.buttonEnviar

        boton.setOnClickListener {

            allFieldsFilled = true

            if (binding.tlfn.text.toString().trim().isEmpty()) {
                binding.tlfnLayout.error = "Escribe un numero de teléfono"
                binding.tlfnLayout.requestFocus()
                allFieldsFilled = false
            }

            if (binding.nombre.text.toString().trim().isEmpty()) {
                binding.nombreLayout.error = "Escribe un nombre"
                binding.nombreLayout.requestFocus()
                allFieldsFilled = false
            }

            if (binding.textInputEditTextDate.text.toString().trim().isEmpty()) {
                binding.textInputLayoutDate.error = "Elige una fecha de nacimiento"
                binding.textInputLayoutDate.requestFocus()
                allFieldsFilled = false
            }

            if (binding.email.text.toString().trim().isEmpty()) {
                binding.emailLayout.error = "Escribe un e-mail"
                binding.emailLayout.requestFocus()
                allFieldsFilled = false
            }

            if(allFieldsFilled === true){
                //sendEmail()
            }

        }

        return binding.root
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                textViewDate.text = selectedDate
            }, year, month, dayOfMonth)

        datePickerDialog.show()

    }

    /*private fun sendEmail() {
        val nombre = binding.nombre.text.toString().trim()
        val fecha = binding.textInputEditTextDate.text.toString().trim()
        val tlfn = binding.tlfn.text.toString().trim()
        val correo = "2grupotxurdinaga@gmail.com"
        val asunto = "UN JUGADOR/A QUIERE REALIZAR UNA PRUEBA"
        val mensaje = "Nombre:  $nombre \n" +
                      "Fecha de Nacimiento:  $fecha \n" +
                      "Número de Teléfono:  $tlfn \n"

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