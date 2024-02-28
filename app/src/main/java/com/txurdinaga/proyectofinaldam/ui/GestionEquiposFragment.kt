package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
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


class GestionEquiposFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var equiposListAdapter: ArrayAdapter<String>
    private lateinit var categoriasList: List<kkCategoryEntity>
    private lateinit var categoriasNameList: List<String>
    private lateinit var ligasList: List<kkLigasEntity>
    private lateinit var ligasNameList: List<String>
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = "Gestión de Equipos"


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        insertMockData()

        //Obtenemos el nombre de las categorias para mostrarlo
        categoriasList = database.kkcategoryDao.getAllCategorias()
        categoriasNameList = categoriasList.map { it.name }
        //Obtenemos el nombre de las ligas para mostrarlo
        ligasList = database.kkligasDao.getAllLigas()
        ligasNameList = ligasList.map { it.name }



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
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        val equipoName = dialogView.findViewById<EditText>(R.id.name)
        val equipoLocation = dialogView.findViewById<EditText>(R.id.location)
        val equipoCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.category)
        val equipoLeague = dialogView.findViewById<AutoCompleteTextView>(R.id.league)
        val equipoNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        val equipoLocationLayout = dialogView.findViewById<TextInputLayout>(R.id.locationLayout)
        val equipoCategoryLayout = dialogView.findViewById<TextInputLayout>(R.id.categoryLayout)
        val equipoLeagueLayout = dialogView.findViewById<TextInputLayout>(R.id.leagueLayout)
        val check_isUnkina = dialogView.findViewById<CheckBox>(R.id.check_isUnkina)
        val check_visible = dialogView.findViewById<CheckBox>(R.id.check_visible)

        var equipo: kkEquiposEntity? = null
        var equipoCategorySelected: String? = null
        var equipoLigaSelected: String? = null
        var dialogTitle = "Alta de Equipo"

        // Adaptador para desplegables de categoria y liga
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasNameList)
        equipoCategory.setAdapter(adapter)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ligasNameList)
        equipoLeague.setAdapter(adapter)


        if(selectedEquipo != "alta"){//no es alta, es MODIFICACION
            equipo = database.kkequipostDao.getEquiposByName(selectedEquipo)
            dialogTitle = "Modificación de Equipo"

            equipoName.setText(equipo.name)
            equipoLocation.setText(equipo.campo)
            check_isUnkina.isChecked = equipo.isUnkina
            check_visible.isChecked = equipo.visible
        }

        builder.setTitle(dialogTitle)
        builder.setPositiveButton("Insertar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()

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


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (equipoName.text.toString().trim().isEmpty()) {
                equipoNameLayout.error = "Escribe un nombre"
                equipoNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                val estaEquipo = database.kkequipostDao.getEquiposByName(equipoName.text.toString())
                if(estaEquipo != null && selectedEquipo == "alta"){
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
                    database.kkequipostDao.insert(kkEquiposEntity(0, equipoName.text.toString(), equipoLocation.text.toString(), equipoCategorySelected?.toInt(), equipoLigaSelected?.toInt(), "escudo1", check_isUnkina.isChecked, check_visible.isChecked))
                } else {
                    if (equipo != null) {
                        equipo.name = equipoName.text.toString()
                        equipo.campo = equipoLocation.text.toString()
                        equipo.categoria = equipoCategorySelected?.toInt()
                        equipo.liga = equipoLigaSelected?.toInt()
                        equipo.escudo = "EscudoNew"
                        equipo.isUnkina = check_isUnkina.isChecked
                        database.kkequipostDao.update(equipo)
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModEquiposDialog(){
        searchList.search(requireContext(), database, "equipo"){selectedEquipo ->
            selectedEquipo?.let {
                showEquiposDialog(selectedEquipo)
            }
        }
    }

    private fun showBajaEquiposDialog(onEquiposSelected: (String) -> Unit) {
        searchList.search(requireContext(), database, "equipo") { ligasSelected ->
            ligasSelected?.let { selectedEquipo ->
                selectedEquipo?.let {
                    onEquiposSelected(it)
                }
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedEquipo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


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

        tv_name.text = selectedEquipo
    }


    private fun insertMockData() {

        val ligas = listOf(
            kkLigasEntity(name = "Senior Masculina"),
            kkLigasEntity(name ="Junior Cadete Mixto Escolar"),
            kkLigasEntity(name = "Infantil Fem.B"),
            kkLigasEntity(name = "Minibasket Femenino D"),
            kkLigasEntity(name =  "Premini 3x3 5x5 D"),


            )
        ligas.forEach { database.kkligasDao.insert(it) }

        val categorias = listOf(
            kkCategoryEntity(name = "Senior Masculina"),
            kkCategoryEntity(name = "Junior Cadete Mixto Escolar"),
            kkCategoryEntity(name = "Infantil Femenino B"),
            kkCategoryEntity(name = "Minibasket Femenino D"),
            kkCategoryEntity(name = "Premini 3x3 5x5 D"),




            )
        categorias.forEach { database.kkcategoryDao.insert(it) }

        val ocupaciones = listOf(
            kkOcupacionesEntity(name =  "Ocupacion1"),
            kkOcupacionesEntity(name = "Ocupacion2"),
            kkOcupacionesEntity(name =  "Ocupacion3"),


            )
        ocupaciones.forEach { database.kkOcupacionesDao.insert(it) }


        // Equipos
        val equipos = listOf(
            kkEquiposEntity(name="UNKINAKO 2",campo= "Usansolo", categoria=1, liga=1, escudo="escudo1", isUnkina =  true, visible =  true),
            kkEquiposEntity(name="ALKIHAIZEA UNKINA",campo= "Usansolo", categoria=2, liga=2, escudo="escudo2", isUnkina =  true, visible =  true),
            kkEquiposEntity(name="KIRAM UNKINA",campo= "Usansolo", categoria=3, liga=3, escudo="escudo3", isUnkina =  true, visible =  true),
            kkEquiposEntity(name="KIRAM UNKINA",campo= "Usansolo", categoria=4, liga=4, escudo="escudo4", isUnkina =  true, visible =  true),
            kkEquiposEntity(name="UNKINA 14",campo= "Usansolo", categoria=5, liga=5, escudo="escudo5", isUnkina =  true, visible =  true),
            kkEquiposEntity(name="UNKINA 15",campo= "Usansolo", categoria=5, liga=5, escudo="escudo6", isUnkina =  true, visible =  true),
            kkEquiposEntity(name="Erandio Altzaga",campo= "Erandio", categoria=2, liga=2, escudo="escudo7", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Erandio Altzaga",campo= "Erandio", categoria=1, liga=1, escudo="escudo8", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Gaztelueta",campo= "Leioa", categoria=2, liga=2, escudo="escudo9", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Gaztelueta",campo= "Leioa", categoria=1, liga=1, escudo="escudo10", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Berangoko Hontzuriak",campo= "Berango", categoria=2, liga=2, escudo="escudo11", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Berangoko Hontzuriak",campo= "Berango", categoria=1, liga=1, escudo="escudo12", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Udalaitz",campo= "Elorrio", categoria=2, liga=2, escudo="escudo13", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Udalaitz",campo= "Elorrio", categoria=1, liga=1, escudo="escudo14", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Trapaga Saskibaloia",campo= "Trapaga", categoria=2, liga=2, escudo="escudo15", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Trapaga Saskibaloia",campo= "Trapaga", categoria=1, liga=1, escudo="escudo16", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Kibuc Basauri",campo= "Basauri", categoria=2, liga=2, escudo="escudo17", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Kibuc Basauri",campo= "Basauri", categoria=1, liga=1, escudo="escudo18", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Tabirako San Antonio",campo= "Durango", categoria=2, liga=2, escudo="escudo19", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Tabirako San Antonio",campo= "Durango", categoria=3, liga=3, escudo="escudo20", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Tabirako San Antonio",campo= "Durango", categoria=5, liga=5, escudo="escudo21", isUnkina =  false, visible =  false),
            kkEquiposEntity(name="Urdaneta",campo= "Loiu", categoria=4, liga=4, escudo="escudo22", isUnkina =  false, visible =  false),

            )
        equipos.forEach { database.kkequipostDao.insert(it) }

        val fotos = listOf(
            kkFotosEntity(title= "img_9116", temporada = "22-23", equipoId = 1, galeria = false),
            kkFotosEntity( title = "img_9130", temporada = "23-24", equipoId = null, galeria = true),
            kkFotosEntity(title = "img_9137", temporada = "22-23", equipoId = null, galeria = true),
            kkFotosEntity(title = "img_9114", temporada = "23-24", equipoId = 3, galeria = false),
            kkFotosEntity(title = "img_9362", temporada = "22-23", equipoId = 2, galeria = false),
            kkFotosEntity(title = "img_9140", temporada = "24-25", equipoId = null, galeria = true),
            kkFotosEntity(title = "img_9154", temporada = "22-23", equipoId = 3, galeria = false),
            kkFotosEntity(title = "img_9159", temporada = "22-23", equipoId = 2, galeria = false),
        )
        fotos.forEach { database.kkfotosDao.insert(it) }


    }
}
