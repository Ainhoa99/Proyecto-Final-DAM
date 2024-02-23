package com.txurdinaga.proyectofinaldam.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionDosBinding
import java.util.Calendar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InscripcionDosFragment : Fragment() {

    private var _binding: FragmentInscripcionDosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInscripcionDosBinding.inflate(layoutInflater, container, false)


        binding.btnDatePicker.setOnClickListener {
            openDatePicker()
        }

        binding.checkParents.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                showEmailSearchDialog()
            }else{
                binding.textInputEmail.isEnabled = true
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, yearSelected, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
            val formattedDate = "%02d/%02d/%d".format(dayOfMonth, monthOfYear + 1, yearSelected)
            binding.btnDatePicker.text = formattedDate
            //Toast.makeText(requireContext(), "Fecha seleccionada: $formattedDate", Toast.LENGTH_SHORT).show()
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    private fun showEmailSearchDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_email, null)
        val textInputEmail = dialogView.findViewById<TextInputEditText>(R.id.textInputEmail)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Escribe el email con el que te registraste la primera vez")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        textInputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterEmails(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun filterEmails(email: String) {
        if(email.isNullOrEmpty()){
            binding.textInputEmail.isEnabled = true
        }else{
            binding.textInputEmail.isEnabled = false
        }
        binding.textInputEmail.setText(email)
    }
}
