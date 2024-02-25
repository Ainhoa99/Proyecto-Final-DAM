package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionEquiposBinding


class GestionEquiposFragment : Fragment() {
    private var _binding: FragmentGestionEquiposBinding? = null
    private val binding get() = _binding!!
    private lateinit var equiposListAdapter: ArrayAdapter<String>
    private lateinit var categoriasList: List<kkCategoryEntity>
    private lateinit var categoriasNameList: List<String>
    private lateinit var ligasList: List<kkLigasEntity>
    private lateinit var ligasNameList: List<String>
    private lateinit var database: kkAppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionEquiposBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = "Gestión de Equipos"


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        //Obtenemos el nombre de las categorias para mostrarlo
        categoriasList = database.kkcategoryDao.getAllTeachers()
        categoriasNameList = categoriasList.map { it.name }
        //Obtenemos el nombre de las categorias para mostrarlo
        ligasList = database.kkligasDao.getAllTeachers()
        ligasNameList = ligasList.map { it.name }

        //insertMockData()


        binding.btnAlta.setOnClickListener() {
            showEquiposDialog("alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaEquiposDialog { selectedEquipo ->
                showConfirmDeleteDialog(selectedEquipo)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModEquiposDialog()
        }

        return binding.root
    }

    private fun showEquiposDialog(selectedEquipo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gestion_equipo, null)

        val equipoName = dialogView.findViewById<EditText>(R.id.name)
        val equipoLocation = dialogView.findViewById<EditText>(R.id.location)
        val equipoCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.category)
        val equipoLeague = dialogView.findViewById<AutoCompleteTextView>(R.id.league)
        val equipoNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        val equipoLocationLayout = dialogView.findViewById<TextInputLayout>(R.id.locationLayout)
        val equipoCategoryLayout = dialogView.findViewById<TextInputLayout>(R.id.categoryLayout)
        val equipoLeagueLayout = dialogView.findViewById<TextInputLayout>(R.id.leagueLayout)

        var equipo: kkEquiposEntity? = null
        var equipoCategorySelected: String? = null
        var equipoLigaSelected: String? = null
        var dialogTitle = "Alta de Equipo"

        // Adaptador para desplegables de categoria y liga
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasNameList)
        equipoCategory.setAdapter(adapter)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ligasNameList)
        equipoLeague.setAdapter(adapter)


        if(selectedEquipo != "alta"){//no es alta, MODIFICACION
            equipo = database.kkequipostDao.getEquiposByName(selectedEquipo)
            dialogTitle = "Modificación de Equipo"

            equipoName.setText(equipo.name)
            equipoLocation.setText(equipo.campo)
        }
        equipoCategory.setOnItemClickListener { parent, view, position, id ->
            equipoCategorySelected = parent.getItemAtPosition(position).toString()
            val categoria = categoriasList.find { it.name == equipoCategorySelected }
            equipoCategorySelected = categoria?.id.toString()
        }
        equipoLeague.setOnItemClickListener { parent, view, position, id ->
            equipoLigaSelected = parent.getItemAtPosition(position).toString()
            val liga = ligasList.find { it.name == equipoLigaSelected }
            equipoLigaSelected = liga?.id.toString()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                var allFieldsFilled = true

                if (equipoName.text.toString().trim().isEmpty()) {
                    equipoNameLayout.error = "Escribe un nombre"
                    equipoNameLayout.requestFocus()
                    allFieldsFilled = false
                }else{//comprobar que esta nombre no este guardado ya
                    val estaEquipo = database.kkequipostDao.getEquiposByName(equipoName.text.toString())
                    if(estaEquipo != null){
                        equipoNameLayout.error = "Ya existe este equipo"
                        equipoNameLayout.requestFocus()
                        allFieldsFilled = false
                    }
                }

                if (equipoLocation.text.toString().trim().isEmpty()) {
                    equipoLocationLayout.error = "Escribe un campo"
                    equipoLocationLayout.requestFocus()
                    allFieldsFilled = false
                }
                if (equipoCategorySelected == null) {
                    equipoCategoryLayout.error = "Selecciona una categoria"
                    equipoCategoryLayout.requestFocus()
                    allFieldsFilled = false
                }
                if (equipoLigaSelected == null) {
                    equipoLeagueLayout.error = "Selecciona una liga"
                    equipoLeagueLayout.requestFocus()
                    allFieldsFilled = false
                }

                if(allFieldsFilled){
                    if (selectedEquipo == "alta") {
                        database.kkequipostDao.insert(kkEquiposEntity(0, equipoName.text.toString(), equipoLocation.text.toString(), equipoCategorySelected?.toInt(), equipoLigaSelected?.toInt(), "escudo1"))
                    } else {
                        if (equipo != null) {
                            equipo.name = equipoName.text.toString()
                            equipo.campo = equipoLocation.text.toString()
                            equipo.categoria = equipoCategorySelected?.toInt()
                            equipo.liga = equipoLigaSelected?.toInt()
                            equipo.escudo = "EscudoNew"
                            database.kkequipostDao.update(equipo)
                        }
                    }
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

    }

    private fun showModEquiposDialog(){
        search { selectedEquipo ->
            selectedEquipo?.let {
                showEquiposDialog(selectedEquipo)
            }
        }
    }

    private fun showBajaEquiposDialog(onEquiposSelected: (String) -> Unit){
        search { selectedEquipo ->
            selectedEquipo?.let {
                onEquiposSelected(it)
            }
        }
    }

    private fun search(onEquiposSelected: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_buscador, null)
        val lv_equiposList = dialogView.findViewById<ListView>(R.id.lv_equiposList)
        val textInputSearchEquipo = dialogView.findViewById<TextInputEditText>(R.id.textInputSearchEquipo)

        val equipos = database.kkequipostDao.getAllEquipos()
        val teacherNames = equipos.map { it.name }
        equiposListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, teacherNames)
        lv_equiposList.adapter = equiposListAdapter

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccione el equipo")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        textInputSearchEquipo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterEquipos(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        lv_equiposList.setOnItemClickListener { _, _, position, _ ->
            val selectedTeacher = equiposListAdapter.getItem(position)
            selectedTeacher?.let {
                onEquiposSelected(it)
                dialog.dismiss()
            }
        }
    }
    private fun filterEquipos(query: String) {
        val equipos = database.kkequipostDao.getAllEquipos()
        val filteredList = equipos.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.name.contains(query, ignoreCase = true)
        }.map { it.name }

        equiposListAdapter.clear()
        equiposListAdapter.addAll(filteredList)
        equiposListAdapter.notifyDataSetChanged()
    }

    private fun showConfirmDeleteDialog(selectedEquipo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_equiponame = dialogView.findViewById<TextView>(R.id.tv_equiponame)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar el equipo?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val equipo = database.kkequipostDao.getEquiposByName(selectedEquipo)
                database.kkequipostDao.delete(equipo)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_equiponame.text = selectedEquipo
    }














    private fun insertMockData() {

        val categorias = listOf(
            kkCategoryEntity(1, "Categoria1", "Mixto"),
            kkCategoryEntity(2, "Categoria2", "Masculino"),
            kkCategoryEntity(3, "Categoria3", "Femenino"),
            kkCategoryEntity(4, "Categoria4", "Masculino"),
            kkCategoryEntity(5, "Categoria5", "Mixto"),


            )
        categorias.forEach { database.kkcategoryDao.insert(it) }

        val ligas = listOf(
            kkLigasEntity(1, "Liga1", "fase1", "grupo1"),
            kkLigasEntity(2, "Liga2", "fase1", "grupo1"),
            kkLigasEntity(3, "Liga3", "fase1", "grupo1"),


            )
        ligas.forEach { database.kkligasDao.insert(it) }
        // Equipos
        val equipos = listOf(
            kkEquiposEntity(1, "Equipo1", "campo1", 1, 1, "escudo1"),
            kkEquiposEntity(2, "Equipo2", "campo2", 2, 1, "escudo1"),
            kkEquiposEntity(3, "Equipo3", "campo3", 3, 2, "escudo1"),
            kkEquiposEntity(4, "Equipo4", "campo4", 2, 1, "escudo1"),
            kkEquiposEntity(5, "Equipo5", "campo5", 4, 2, "escudo1"),
            kkEquiposEntity(6, "Equipo6", "campo6", 1, 2, "escudo1"),
            kkEquiposEntity(7, "Equipo7", "campo7", 5, 3, "escudo1"),
            kkEquiposEntity(8, "Equipo8", "campo8", 3, 2, "escudo1"),
            kkEquiposEntity(9, "Equipo9", "campo9", 3, 3, "escudo1")

        )
        equipos.forEach { database.kkequipostDao.insert(it) }


    }
}