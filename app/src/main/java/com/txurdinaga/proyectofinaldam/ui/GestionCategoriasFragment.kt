package com.txurdinaga.proyectofinaldam.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.data.model.Category
import com.txurdinaga.proyectofinaldam.data.repo.CategoryRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.SearchList
import kotlinx.coroutines.launch


class GestionCategoriasFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)
    private lateinit var listAdapter: ArrayAdapter<Pair<Int?, String?>>

    /**
     * Ejemplo uso de repositorios
     */
    private lateinit var categoryRepo: CategoryRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = getString(R.string.gestion_categorias)

        /**
         * Ejemplo uso de repositorios
         */
        categoryRepo = CategoryRepository()

        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showCategoriasDialog(null, "alta")
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

    private fun showCategoriasDialog(selectedCategoria: Pair<Int?, String?>?, modo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var categoria: kkCategoryEntity? = null
        val categoriaName = dialogView.findViewById<EditText>(R.id.name)
        val categoriaNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Categoria"
        var allCategories: List<Category>
        var category: Category = Category(0, "Error")


        if (modo == "modificacion" && selectedCategoria != null) {//no es alta, es MODIFICACION
            //categoria = database.kkcategoryDao.getCategoryById(selectedCategoria.first)
            lifecycleScope.launch {
                try {
                    allCategories = categoryRepo.getAllCategories()
                    allCategories.forEach { catego ->
                        if (catego.categoryName == selectedCategoria.second) {
                            category = catego
                            categoriaName.setText(category.categoryName)
                            return@forEach
                        }
                    }
                } catch (loginE: LoginError) {
                    // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                } catch (getAllE: GetAllError) {
                    // Mostrar mensaje de error sobre problemas generales durante la creación
                }

            }
            dialogTitle = "Modificación de Categoria"
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
            } else {//comprobar que esta nombre no este guardado ya
                var estaCategoria = false//database.kkcategoryDao.getCategoryByName(categoriaName.text.toString())
                lifecycleScope.launch {
                    try {
                        val allCategories = categoryRepo.getAllCategories()
                        allCategories.forEach { catego ->
                            if (selectedCategoria != null) {
                                if (catego.categoryName == selectedCategoria.second) {
                                    estaCategoria = true
                                    return@forEach
                                }
                            }
                        }
                    } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }
                if (estaCategoria && modo == "alta") {
                    categoriaNameLayout.error = "Ya existe esta categoria"
                    categoriaNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }

            if (allFieldsFilled) {
                if (modo == "alta") {
                    //database.kkcategoryDao.insert(kkCategoryEntity(name = categoriaName.text.toString()))
                    /**
                     * Ejemplo uso de repositorios
                     */
                    category = Category(categoryName = categoriaName.text.toString())
                    lifecycleScope.launch {
                        try {
                            categoryRepo.create(category)
                        } catch (loginE: LoginError) {
                            // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                        } catch (createE: CreateError) {
                            // Mostrar mensaje de error sobre problemas generales durante la creación
                        }

                    }
                } else {
                    lifecycleScope.launch {
                        try {
                            category.categoryName = categoriaName.text.toString()
                            categoryRepo.update(category)
                        } catch (loginE: LoginError) {
                        // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                        } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                        }
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModCategoriasDialog() {
        search(requireContext()) { categoriaSelected ->
            categoriaSelected?.let {
                showCategoriasDialog(categoriaSelected, "modificacion")
            }
        }
    }

    private fun showBajaCategoriasDialog(onCategoriaSelected: (Pair<Int?, String?>) -> Unit) {
        search(requireContext()) { categoriaSelected ->
            categoriaSelected?.let {
                onCategoriaSelected(it)
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedCategoria: Pair<Int?, String?>) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la categoria?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                //val categoria = database.kkcategoryDao.getCategoryById(selectedCategoria.first)
                lifecycleScope.launch {
                    try {
                        val allCategories = categoryRepo.getAllCategories()
                        allCategories.forEach { catego ->
                            if (catego.categoryName == selectedCategoria.second) {
                                categoryRepo.delete(catego)
                            }
                        }
                    } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }
               // database.kkcategoryDao.delete(categoria)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedCategoria.second
    }


    private fun search(
        context: Context,
        onLigasSelected: (Pair<Int?, String?>) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_buscador, null)
        val lv_List = dialogView.findViewById<ListView>(R.id.lv_List)
        val textInputSearch = dialogView.findViewById<TextInputEditText>(R.id.textInputSearch)
        var listNames: List<Pair<Int?, String?>> = listOf()
        var title = "Gestión"
        lifecycleScope.launch {
            try {
                val lista = categoryRepo.getAllCategories()
                listNames = lista.map { Pair(it.categoryId, it.categoryName) }
                title = "Modificación de Categoria"
            } catch (createE: CreateError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }

            listAdapter = ArrayAdapter<Pair<Int?, String?>>(
                context,
                android.R.layout.simple_list_item_1,
                listNames
            )
            lv_List.adapter = listAdapter

            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton("Aceptar") { dialog, _ -> dialog.cancel() }
                .create()
            dialog.show()

            textInputSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    filter(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            lv_List.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = listAdapter.getItem(position)
                selectedItem?.let {
                    onLigasSelected(it)
                    dialog.dismiss()
                }
            }
        }
    }

    private fun filter(query: String) {
        //Ahora mismo esta repetido en varias lista por problemas de tipos
        var filteredList: List<Pair<Int, String>> = mutableListOf()


        var list: List<Category> = listOf()
        lifecycleScope.launch {
            try {
                list = categoryRepo.getAllCategories()
            } catch (createE: CreateError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
            filteredList = list.filter {
                it.categoryName!!.contains(query, ignoreCase = true) ||
                        it.categoryName!!.contains(query, ignoreCase = true)
            }.map { Pair(it.categoryId, it.categoryName) } as List<Pair<Int, String>>
        }
        listAdapter.clear()
        listAdapter.addAll(filteredList)
        listAdapter.notifyDataSetChanged()
    }
}