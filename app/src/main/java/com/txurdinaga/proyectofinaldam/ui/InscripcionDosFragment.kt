package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.data.model.User
import com.txurdinaga.proyectofinaldam.data.repo.UserRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentInscripcionDosBinding
import com.txurdinaga.proyectofinaldam.util.GetAllError
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class InscripcionDosFragment : Fragment() {

    private var _binding: FragmentInscripcionDosBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepo: UserRepository
    private lateinit var boton: Button
    private lateinit var empadronamiento : Uri
    private lateinit var diploma :Uri
    private lateinit var certificado :Uri
    private var comprFecha = false

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
        _binding = FragmentInscripcionDosBinding.inflate(layoutInflater, container, false)
        userRepo = UserRepository()

        binding.btnDatePicker.setOnClickListener {
            openDatePicker()
        }

        binding.checkParents.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showEmailSearchDialog()
            } else {
                binding.textInputEmail.isEnabled = true
            }
        }

        binding.btnEmpadronamiento.setOnClickListener {
            subirPDF(binding.btnEmpadronamiento)
        }
        binding.btnDiploma.setOnClickListener {
            subirPDF(binding.btnDiploma)
        }
        binding.btnCertificado.setOnClickListener {
            subirPDF(binding.btnCertificado)
        }

        binding.btnInscribir.setOnClickListener {

            if (
                !binding.name.text.isNullOrEmpty() &&
                !binding.surname.text.isNullOrEmpty() &&
                !binding.textInputEmail.text.isNullOrEmpty() &&
                comprFecha &&
                ::diploma.isInitialized &&
                ::empadronamiento.isInitialized &&
                ::certificado.isInitialized
            ){
                lifecycleScope.launch {
                    try {
                        val user = User(
                            name = binding.name.text.toString(),
                            surname =binding.surname.text.toString(),
                            dateOfBirth = convertirALong(binding.btnDatePicker.text.toString()),
                            email = binding.textInputEmail.text.toString()
                        )
                        //userRepo.register(user)
                        Toast.makeText(context,R.string.confirmacion_inscripcion,Toast.LENGTH_LONG).show()
                    } catch (getAllE: GetAllError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }
                //datos enviar email
                binding.name.text
                binding.surname.text
                binding.btnDatePicker.text
                binding.checkParents.isChecked
                binding.textInputEmail.text
                binding.dni.text
                diploma
                empadronamiento
                certificado
            }else{
                Toast.makeText(context, R.string.datos_requeridos, Toast.LENGTH_SHORT).show()
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

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, yearSelected, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(yearSelected, monthOfYear, dayOfMonth)
                val formattedDate = "%02d/%02d/%d".format(dayOfMonth, monthOfYear + 1, yearSelected)

                binding.btnDatePicker.text = "$formattedDate"
                comprFecha = true
                //Toast.makeText(requireContext(), "Fecha seleccionada: $formattedDate", Toast.LENGTH_SHORT).show()
            }, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    private fun convertirALong(formattedDate: String) :Long{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.parse(formattedDate)
        return date.time / 1000
    }

    private fun showEmailSearchDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_email, null)
        val textInputEmail = dialogView.findViewById<TextInputEditText>(R.id.textInputEmail)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.mensajito_email_primera_vez))
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
        if (email.isNullOrEmpty()) {
            binding.textInputEmail.isEnabled = true
        } else {
            binding.textInputEmail.isEnabled = false
        }
        binding.textInputEmail.setText(email)
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
                    val nombreArchivo =
                        cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                    boton.text = "${boton.text.toString().split(" ")[0]} : $nombreArchivo"
                    when(boton.text.toString().split(" ")[0]){
                        "Empadronamiento" -> empadronamiento = uri
                        "Diploma" -> diploma = uri
                        "Certificado" -> certificado = uri
                        "Erresidentzi" -> empadronamiento = uri
                        "Egiaztapena" -> certificado = uri

                    }
                }
            }
        }
    }
}
