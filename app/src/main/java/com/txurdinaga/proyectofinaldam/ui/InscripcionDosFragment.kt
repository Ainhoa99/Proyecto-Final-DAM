package com.txurdinaga.proyectofinaldam.ui

import DialogSearchParent
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionDosBinding
import java.util.Calendar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.ArrayAdapter

class InscripcionDosFragment : Fragment() {

    private var _binding: FragmentInscripcionDosBinding? = null
    private val binding get() = _binding!!
    private lateinit var emailsListAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInscripcionDosBinding.inflate(layoutInflater, container, false)

        binding.textInputLayoutDeleteTeacher.setEndIconOnClickListener {
            showTeacherSearchDialog { selectedEmail ->
                binding.textInputDeleteTeacher.setText(selectedEmail)
                binding.textInputDeleteTeacher.error = null
            }
        }

        binding.btnDatePicker.setOnClickListener {
            openDatePicker()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showTeacherSearchDialog(onTeacherSelected: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_teacher_search, null)
        val textInputTeacherSearch = dialogView.findViewById<TextInputEditText>(R.id.textInputTeacherSearchBar)
        val emailListView = dialogView.findViewById<ListView>(R.id.teacher_list)


        /**
         * Filtro para buscar si ese email esta en uso o no, hay que sustituir
         * el array de emails por los email que vengan del servidor.
         * Esta llamada se necesita en dos sitios(simplificar para que solo sea en uno)
         *
         */
        val emails = arrayOf(
            "Juan", "María", "Pedro", "Ana", "Luis", "Elena", "Carlos", "Laura", "Miguel", "Sofía",
            "David", "Isabel", "Javier", "Carmen", "José", "Paula", "Antonio", "Lucía", "Pablo", "Eva"
        )

        val userEmails = emails.map { it }
        emailsListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userEmails)
        emailListView.adapter = emailsListAdapter

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Lista de Emails")
            .setView(dialogView)
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        textInputTeacherSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterEmails(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        emailListView.setOnItemClickListener { _, _, position, _ ->
            val selectedEmail = emailsListAdapter.getItem(position)
            selectedEmail?.let {
                onTeacherSelected(it)
                dialog.dismiss()
            }
        }
    }

    private fun filterEmails(query: String) {
        val allTeachers = arrayOf(
            "Juan", "María", "Pedro", "Ana", "Luis", "Elena", "Carlos", "Laura", "Miguel", "Sofía",
            "David", "Isabel", "Javier", "Carmen", "José", "Paula", "Antonio", "Lucía", "Pablo", "Eva"
        )
        val filteredList = allTeachers.filter {
            it.contains(query, ignoreCase = true) ||
                    it.contains(query, ignoreCase = true)
        }.map { it }

        emailsListAdapter.clear()
        emailsListAdapter.addAll(filteredList)
        emailsListAdapter.notifyDataSetChanged()
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
}
