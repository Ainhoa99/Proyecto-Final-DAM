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
                    database.kkequipostDao.insert(kkEquiposEntity(0, equipoName.text.toString(), equipoLocation.text.toString(), equipoCategorySelected?.toLong(), equipoLigaSelected?.toLong(), "escudo1", check_isUnkina.isChecked, check_visible.isChecked))
                } else {
                    if (equipo != null) {
                        equipo.name = equipoName.text.toString()
                        equipo.campo = equipoLocation.text.toString()
                        equipo.categoria = equipoCategorySelected?.toLong()
                        equipo.liga = equipoLigaSelected?.toLong()
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
        añadirCategorias()
        añadirLigas()
        categoriasList = database.kkcategoryDao.getAllCategorias()
        categoriasNameList = categoriasList.map { it.name }
        ligasList = database.kkligasDao.getAllLigas()
        ligasNameList = ligasList.map { it.name }
        añadirEquipos()
    }

    private fun añadirLigas() {
        database.kkligasDao.insert(kkLigasEntity(id = 1, name = "Senior Masculina"))
        database.kkligasDao.insert(kkLigasEntity(id = 2, name = "Junior Cadete Mixto Escolar"))
        database.kkligasDao.insert(kkLigasEntity(id = 3, name = "Infantil Fem.B"))
        database.kkligasDao.insert(kkLigasEntity(id = 4, name = "Minibasket Femenino D"))
        database.kkligasDao.insert(kkLigasEntity(id = 5, name = "Premini 3x3 5x5 D"))
    }

    /*private fun eliminarCategorias() {
        database.kkcategoryDao.deleteCategorias()

    }*/

    private fun añadirCategorias() {
        database.kkcategoryDao.insert(kkCategoryEntity(id = 1, name = "Senior Masculina"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 2, name = "Junior Cadete Mixto Escolar"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 3, name = "Infantil Femenino B"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 4, name = "Minibasket Femenino D"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 5, name = "Premini 3x3 5x5 D"))
    }

    /*private fun eliminarEquipos() {
        database.kkequipostDao.deleteEquipo()
    }*/

    private fun añadirEquipos() {
        // Buscar los ids de categoría y liga para Senior Masculino
        val seniorMasculinoCategoriaId = categoriasList.find { it.id.toInt() == 1 }?.id ?: 0
        val seniorMasculinoLigaId = ligasList.find { it.id.toInt() == 1 }?.id ?: 0

        // Buscar los ids de categoría y liga para Junior Masculino
        val juniorMasculinoCategoriaId = categoriasList.find { it.id.toInt() == 2 }?.id ?: 0
        val juniorMasculinoLigaId = ligasList.find { it.id.toInt() == 2 }?.id ?: 0

        // Buscar los ids de categoría y liga para Senior Masculino
        val infantilFemeninoCategoriaId = categoriasList.find { it.id.toInt() == 3 }?.id ?: 0
        val infantilFemeninoLigaId = ligasList.find { it.id.toInt() == 3 }?.id ?: 0

        // Buscar los ids de categoría y liga para Junior Masculino
        val minibasketFemeninoCategoriaId = categoriasList.find { it.id.toInt() == 4 }?.id ?: 0
        val minibasketFemeninoLigaId = ligasList.find { it.id.toInt() == 4 }?.id ?: 0

        // Buscar los ids de categoría y liga para Senior Masculino
        val preminiCategoriaId = categoriasList.find { it.id.toInt() == 5 }?.id ?: 0
        val preminiLigaId = ligasList.find { it.id.toInt() == 5 }?.id ?: 0


        // Crear instancias de Equipo y insertar en la base de datos
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 1,
                name = "UNKINAKO 2",
                campo = "Usansolo",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 2,
                name = "ALKIHAIZEA UNKINA",
                campo = "Usansolo",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 3,
                name = "KIRAM UNKINA",
                campo = "Usansolo",
                categoria = infantilFemeninoCategoriaId,
                liga = infantilFemeninoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 4,
                name = "KIRAM UNKINA",
                campo = "Usansolo",
                categoria = minibasketFemeninoCategoriaId,
                liga = minibasketFemeninoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 5,
                name = "UNKINA 14",
                campo = "Usansolo",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 6,
                name = "UNKINA 15",
                campo = "Usansolo",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 7,
                name = "Erandio Altzaga",
                campo = "Erandio",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 8,
                name = "Erandio Altzaga",
                campo = "Erandio",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 9,
                name = "Gaztelueta",
                campo = "Leioa",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 10,
                name = "Gaztelueta",
                campo = "Leioa",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 11,
                name = "Berangoko Hontzuriak",
                campo = "Berango",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 12,
                name = "Berangoko Hontzuriak",
                campo = "Berango",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 13,
                name = "Udalaitz",
                campo = "Elorrio",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 14,
                name = "Udalaitz",
                campo = "Elorrio",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 15,
                name = "Trapaga Saskibaloia",
                campo = "Trapaga",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 16,
                name = "Trapaga Saskibaloia",
                campo = "Trapaga",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 17,
                name = "Kibuc Basauri",
                campo = "Basauri",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 18,
                name = "Kibuc Basauri",
                campo = "Basauri",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 19,
                name = "Tabirako San Antonio",
                campo = "Durango",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 20,
                name = "Tabirako Kurutxiaga",
                campo = "Durango",
                categoria = infantilFemeninoCategoriaId,
                liga = infantilFemeninoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 21,
                name = "Urdaneta",
                campo = "Loiu",
                categoria = minibasketFemeninoCategoriaId,
                liga = minibasketFemeninoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 22,
                name = "Tabirako San Antonio",
                campo = "Durango",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 23,
                name = "Tabirako San Antonio",
                campo = "Loiu",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 23,
                name = "Uribike Ugeraga",
                campo = "Sopelana",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 24,
                name = "Berriz SBT",
                campo = "Berriz",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
    }
}