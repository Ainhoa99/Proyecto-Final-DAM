package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class GestionFotosFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = "Gestión de Fotos"


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showFotosDialog(null, "alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaFotosDialog { selectedFoto ->
                showConfirmDeleteDialog(selectedFoto)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModFotosDialog()
        }

        return binding.root
    }

    private fun showFotosDialog(selectedFotos: Pair<Int, String>?, modo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var foto: kkFotosEntity? = null
        val fotoName = dialogView.findViewById<EditText>(R.id.name)
        val fotoNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Fotos"


        if(modo != "alta" &&selectedFotos!=null){//no es alta, es MODIFICACION
            foto = database.kkfotosDao.getFotosById(selectedFotos.first)
            dialogTitle = "Modificación de Fotos"

            fotoName.setText(foto.title)
        }

        builder.setTitle(dialogTitle)
        builder.setPositiveButton("Insertar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (fotoName.text.toString().trim().isEmpty()) {
                fotoNameLayout.error = "Escribe un nombre"
                fotoNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                val estaFoto = database.kkfotosDao.getFotosByTitle(fotoName.text.toString())
                if(estaFoto.isNotEmpty() && modo == "alta"){
                    fotoNameLayout.error = "Ya existe esta foto"
                    fotoNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }

            if(allFieldsFilled){
                if (modo == "alta") {
                    database.kkfotosDao.insert(kkFotosEntity(title="l",temporada= "", equipoId= 3, galeria = false))
                } else {
                    if (foto != null) {
                        foto.title = fotoName.text.toString()
                        database.kkfotosDao.update(foto)
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModFotosDialog(){
        searchList.search(requireContext(), database, "foto"){ fotosSelected ->
            fotosSelected?.let {
                showFotosDialog(fotosSelected, "modificacion")
            }
        }
    }

    private fun showBajaFotosDialog(onFotosSelected: ((Pair<Int, String>)) -> Unit) {
        searchList.search(requireContext(), database, "foto") { fotosSelected ->
            fotosSelected?.let {
                onFotosSelected(it)
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedFoto: (Pair<Int, String>)) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la foto?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val foto = database.kkfotosDao.getFotosById(selectedFoto.first)
                database.kkfotosDao.delete(foto)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedFoto.second
    }
}