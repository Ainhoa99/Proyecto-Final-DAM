package com.txurdinaga.proyectofinaldam.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPruebaBinding.inflate(layoutInflater, container, false)

        textViewDate = binding.textInputEditTextDate

        textViewDate.setOnClickListener {
            showDatePicker()
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
}