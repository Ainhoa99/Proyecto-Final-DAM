package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.SearchList


class GestionOcupacionesFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = getString(R.string.gestion_ocupaciones)


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showOcupacionesDialog(null, "alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaLigasDialog { selectedOcupacion ->
                showConfirmDeleteDialog(selectedOcupacion)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModOcupacionesDialog()
        }

        return binding.root
    }

    private fun showOcupacionesDialog(selectedOcupacion: Pair<Int, String>?, modo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var ocupacion: kkOcupacionesEntity? = null
        val ocupacionName = dialogView.findViewById<EditText>(R.id.name)
        val ocupacionNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Ocupación"


        if(modo == "modificacion" && selectedOcupacion!=null){//no es alta, es MODIFICACION
            ocupacion = database.kkOcupacionesDao.getOcupacionById(selectedOcupacion.first)
            dialogTitle = "Modificación de Ocupación"

            ocupacionName.setText(ocupacion.name)
        }

        builder.setTitle(dialogTitle)
        builder.setPositiveButton("Insertar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (ocupacionName.text.toString().trim().isEmpty()) {
                ocupacionNameLayout.error = "Escribe un nombre"
                ocupacionNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                val estaOcupacion = database.kkOcupacionesDao.getOcupacionByName(ocupacionName.text.toString())
                if(estaOcupacion.isNotEmpty() && modo == "alta"){
                    ocupacionNameLayout.error = "Ya existe esta ocupacion"
                    ocupacionNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }

            if(allFieldsFilled){
                if (modo == "alta") {
                    database.kkOcupacionesDao.insert(kkOcupacionesEntity(name= ocupacionName.text.toString()))
                } else {
                    if (ocupacion != null) {
                        ocupacion.name = ocupacionName.text.toString()
                        database.kkOcupacionesDao.update(ocupacion)
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModOcupacionesDialog(){
        searchList.search(requireContext(), database, "ocupacion"){ ocupacionesSelected ->
            ocupacionesSelected?.let {
                showOcupacionesDialog(ocupacionesSelected, "modificacion")
            }
        }
    }

    private fun showBajaLigasDialog(onOcupacionSelected: (Pair<Int, String>) -> Unit) {
        searchList.search(requireContext(), database, "ocupacion") { ocupacionesSelected ->
            ocupacionesSelected?.let {
                onOcupacionSelected(it)
            }
        }
    }


     private fun showConfirmDeleteDialog(selectedOcupacion: Pair<Int, String>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la ocupación?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val ocupacion = database.kkOcupacionesDao.getOcupacionById(selectedOcupacion.first)
                database.kkOcupacionesDao.delete(ocupacion)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedOcupacion.second
    }
}