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


class GestionCategoriasFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = "Gestión de Categorias"


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showCategoriasDialog("alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaCategoriasDialog { selectedCategoria ->
                showConfirmDeleteDialog(selectedCategoria)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModCategoriasDialog()
        }

        return binding.root
    }

    private fun showCategoriasDialog(selectedCategoria: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var categoria: kkCategoryEntity? = null
        val categoriaName = dialogView.findViewById<EditText>(R.id.name)
        val categoriaNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Categoria"


        if(selectedCategoria != "alta"){//no es alta, es MODIFICACION
            categoria = database.kkcategoryDao.getTeacherByName(selectedCategoria)
            dialogTitle = "Modificación de Categoria"

            categoriaName.setText(categoria.name)
        }

        builder.setTitle(dialogTitle)
        builder.setPositiveButton("Insertar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (categoriaName.text.toString().trim().isEmpty()) {
                categoriaNameLayout.error = "Escribe un nombre"
                categoriaNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                val estaCategoria = database.kkcategoryDao.getTeacherByName(categoriaName.text.toString())
                if(estaCategoria != null && selectedCategoria == "alta"){
                    categoriaNameLayout.error = "Ya existe esta categoria"
                    categoriaNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }

            if(allFieldsFilled){
                if (selectedCategoria == "alta") {
                    database.kkcategoryDao.insert(kkCategoryEntity(0, categoriaName.text.toString()))
                } else {
                    if (categoria != null) {
                        categoria.name = categoriaName.text.toString()
                        database.kkcategoryDao.update(categoria)
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModCategoriasDialog(){
        searchList.search(requireContext(), database, "categoria"){ categoriaSelected ->
            categoriaSelected?.let {
                showCategoriasDialog(categoriaSelected)
            }
        }
    }

    private fun showBajaCategoriasDialog(onCategoriaSelected: (String) -> Unit) {
        searchList.search(requireContext(), database, "categoria") { categoriaSelected ->
            categoriaSelected?.let {
                onCategoriaSelected(it)
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedCategoria: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la categoria?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val categoria = database.kkcategoryDao.getTeacherByName(selectedCategoria)
                database.kkcategoryDao.delete(categoria)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedCategoria
    }
}