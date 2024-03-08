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
import com.txurdinaga.proyectofinaldam.data.model.Role
import com.txurdinaga.proyectofinaldam.data.repo.RoleRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.SearchList
import kotlinx.coroutines.launch


class GestionOcupacionesFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    private lateinit var listAdapter: ArrayAdapter<Pair<Int?, String?>>

    /**
     * Ejemplo uso de repositorios
     */
    private lateinit var roleRepo: RoleRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = getString(R.string.gestion_ocupaciones)

        /**
         * Ejemplo uso de repositorios
         */
        roleRepo = RoleRepository()

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
            showBajaOcupacionesDialog { selectedOcupacion ->
                showConfirmDeleteDialog(selectedOcupacion)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModOcupacionesDialog()
        }

        return binding.root
    }

    private fun showOcupacionesDialog(selectedOcupacion: Pair<Int?, String?>?, modo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var ocupacion: kkOcupacionesEntity? = null
        val ocupacionName = dialogView.findViewById<EditText>(R.id.name)
        val ocupacionNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Ocupación"
        var allRoles: List<Role>
        var role: Role = Role(0, "Error")


        if (modo == "modificacion" && selectedOcupacion != null) {//no es alta, es MODIFICACION
            //ocupacion = database.kkOcupacionesDao.getOcupacionById(selectedOcupacion.first)

            lifecycleScope.launch {
                try {
                    allRoles = roleRepo.getAllRoles()
                    allRoles.forEach { rol ->
                        if (rol.roleName == selectedOcupacion.second) {
                            role = rol
                            ocupacionName.setText(role.roleName)
                            return@forEach
                        }
                    }
                } catch (loginE: LoginError) {
                    // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                } catch (getAllE: GetAllError) {
                    // Mostrar mensaje de error sobre problemas generales durante la creación
                }

            }
            dialogTitle = "Modificación de Ocupación"

            //ocupacionName.setText(ocupacion.name)
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
            } else {//comprobar que esta nombre no este guardado ya
                var estaOcupacion =
                    false //database.kkOcupacionesDao.getOcupacionByName(ocupacionName.text.toString())

                lifecycleScope.launch {
                    try {
                        val allRoles = roleRepo.getAllRoles()
                        allRoles.forEach { rol ->
                            if (selectedOcupacion != null) {
                                if (rol.roleName == selectedOcupacion.second) {
                                    estaOcupacion = true
                                    return@forEach
                                }
                            }
                        }
                    } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }

                    if (estaOcupacion && modo == "alta") {
                        ocupacionNameLayout.error = "Ya existe esta ocupacion"
                        ocupacionNameLayout.requestFocus()
                        allFieldsFilled = false
                    }
                }

                if (allFieldsFilled) {
                    if (modo == "alta") {
                        //database.kkOcupacionesDao.insert(kkOcupacionesEntity(name= ocupacionName.text.toString()))
                        /**
                         * Ejemplo uso de repositorios
                         */
                        role = Role(roleId = 0, roleName = ocupacionName.text.toString())
                        lifecycleScope.launch {
                            try {
                                roleRepo.create(role)
                            } catch (loginE: LoginError) {
                                // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                            } catch (createE: CreateError) {
                                // Mostrar mensaje de error sobre problemas generales durante la creación
                            }

                        }
                    } else {
                        lifecycleScope.launch {
                            try {
                                role.roleName = ocupacionName.text.toString()
                                roleRepo.update(role)
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
    }

    private fun showModOcupacionesDialog() {
        search(requireContext()) { ocupacionSelected ->
            ocupacionSelected?.let {
                showOcupacionesDialog(ocupacionSelected, "modificacion")
            }
        }
    }

    private fun showBajaOcupacionesDialog(onOcupacionSelected: (Pair<Int?, String?>) -> Unit) {
        search(requireContext()) { ocupacionSelected ->
            ocupacionSelected?.let {
                onOcupacionSelected(it)
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedOcupacion: Pair<Int?, String?>) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la ocupación?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                //val ocupacion = database.kkOcupacionesDao.getOcupacionById(selectedOcupacion.first)

                lifecycleScope.launch {
                    try {
                        val allCategories = roleRepo.getAllRoles()
                        allCategories.forEach { rol ->
                            if (rol.roleName == selectedOcupacion.second) {
                                roleRepo.delete(rol)
                            }
                        }
                    } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }

                //database.kkOcupacionesDao.delete(ocupacion)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedOcupacion.second
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
                val lista = roleRepo.getAllRoles()
                listNames = lista.map { Pair(it.roleId, it.roleName) }
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


        var list: List<Role> = listOf()
        lifecycleScope.launch {
            try {
                list = roleRepo.getAllRoles()
            } catch (createE: CreateError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
            filteredList = list.filter {
                it.roleName!!.contains(query, ignoreCase = true) ||
                        it.roleName!!.contains(query, ignoreCase = true)
            }.map { Pair(it.roleId, it.roleName) } as List<Pair<Int, String>>
        }
        listAdapter.clear()
        listAdapter.addAll(filteredList)
        listAdapter.notifyDataSetChanged()
    }
}