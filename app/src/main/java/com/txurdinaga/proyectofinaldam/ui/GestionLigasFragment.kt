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
import com.txurdinaga.proyectofinaldam.data.model.League
import com.txurdinaga.proyectofinaldam.data.repo.CategoryRepository
import com.txurdinaga.proyectofinaldam.data.repo.LeageRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.SearchList
import kotlinx.coroutines.launch


class GestionLigasFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)
    private lateinit var listAdapter: ArrayAdapter<Pair<Int?, String?>>

    private lateinit var leagueRepo: LeageRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = getString(R.string.gestion_ligas)


        leagueRepo = LeageRepository()

        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showLigasDialog(null, "alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaLigasDialog { selectedLiga ->
                showConfirmDeleteDialog(selectedLiga)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModLigasDialog()
        }

        return binding.root
    }

    private fun showLigasDialog(selectedLiga: Pair<Int?, String?>?, modo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var liga: kkLigasEntity? = null
        val ligaName = dialogView.findViewById<EditText>(R.id.name)
        val ligaNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Liga"
        var allLeagues: List<League>
        var league: League = League(0, "Error")


        if(modo == "modificacion" && selectedLiga!=null){//no es alta, es MODIFICACION
            lifecycleScope.launch {
                try {
                    allLeagues = leagueRepo.getAllLeagues()
                    allLeagues.forEach { leag ->
                        if (leag.leagueName == selectedLiga.second) {
                            league = leag
                            ligaName.setText(league.leagueName)
                            return@forEach
                        }
                    }
                } catch (loginE: LoginError) {
                    // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                } catch (getAllE: GetAllError) {
                    // Mostrar mensaje de error sobre problemas generales durante la creación
                }

            }
            dialogTitle = "Modificación de Liga"

            ligaName.setText(league.leagueName)
        }

        builder.setTitle(dialogTitle)
        builder.setPositiveButton("Insertar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (ligaName.text.toString().trim().isEmpty()) {
                ligaNameLayout.error = "Escribe un nombre"
                ligaNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                var estaLeague = false//database.kkcategoryDao.getCategoryByName(categoriaName.text.toString())
                lifecycleScope.launch {
                    try {
                        val allLeagues = leagueRepo.getAllLeagues()
                        allLeagues.forEach { leag ->
                            if (selectedLiga != null) {
                                if (leag.leagueName == selectedLiga.second) {
                                    estaLeague = true
                                    return@forEach
                                }
                            }
                        }
                    } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }
                if (estaLeague && modo == "alta") {
                    ligaNameLayout.error = "Ya existe esta liga"
                    ligaNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }

            if(allFieldsFilled){
                if (modo == "alta") {
                    //database.kkcategoryDao.insert(kkCategoryEntity(name = categoriaName.text.toString()))
                    /**
                     * Ejemplo uso de repositorios
                     */
                    league = League(leagueName = ligaName.text.toString())
                    lifecycleScope.launch {
                        try {
                            leagueRepo.create(league)
                        } catch (loginE: LoginError) {
                            // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                        } catch (createE: CreateError) {
                            // Mostrar mensaje de error sobre problemas generales durante la creación
                        }

                    }
                } else {
                    lifecycleScope.launch {
                        try {
                            league.leagueName = ligaName.text.toString()
                            leagueRepo.update(league)
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

    private fun showModLigasDialog(){
        search(requireContext()){ ligasSelected ->
            ligasSelected?.let {
                showLigasDialog(ligasSelected, "modificacion")
            }
        }
    }

    private fun showBajaLigasDialog(onLigasSelected: (Pair<Int?, String?>) -> Unit) {
       search(requireContext()) { ligasSelected ->
            ligasSelected?.let {
                onLigasSelected(it)
            }
        }
    }


     private fun showConfirmDeleteDialog(selectedLiga: Pair<Int?, String?>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la liga?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val allCategories = leagueRepo.getAllLeagues()
                        allCategories.forEach { leag ->
                            if (leag.leagueName == selectedLiga.second) {
                                leagueRepo.delete(leag)
                            }
                        }
                    } catch (createE: CreateError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedLiga.second
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
                val lista = leagueRepo.getAllLeagues()
                listNames = lista.map { Pair(it.leagueId, it.leagueName) }
                title = "Modificación de Liga"
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


        var list: List<League> = listOf()
        lifecycleScope.launch {
            try {
                list = leagueRepo.getAllLeagues()
            } catch (createE: CreateError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
            filteredList = list.filter {
                it.leagueName!!.contains(query, ignoreCase = true) ||
                        it.leagueName!!.contains(query, ignoreCase = true)
            }.map { Pair(it.leagueId, it.leagueName) } as List<Pair<Int, String>>
        }
        listAdapter.clear()
        listAdapter.addAll(filteredList)
        listAdapter.notifyDataSetChanged()
    }
}