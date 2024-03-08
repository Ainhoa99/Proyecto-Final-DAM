package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
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
import com.txurdinaga.proyectofinaldam.data.model.ImageMetadata
import com.txurdinaga.proyectofinaldam.data.model.League
import com.txurdinaga.proyectofinaldam.data.model.Team
import com.txurdinaga.proyectofinaldam.data.repo.CategoryRepository
import com.txurdinaga.proyectofinaldam.data.repo.ImageRepository
import com.txurdinaga.proyectofinaldam.data.repo.LeageRepository
import com.txurdinaga.proyectofinaldam.data.repo.TeamRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.SearchList
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class GestionEquiposFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var equiposListAdapter: ArrayAdapter<String>
    private lateinit var categoriasList: List<Category>
    private lateinit var categoriasNameList: List<String>
    private lateinit var ligasList: List<League>
    private lateinit var ligasNameList: List<String>
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)
    private val PICK_IMAGE_REQUEST = 1
    private var imageString: String=""
    private lateinit var listAdapter: ArrayAdapter<Pair<Int?, String?>>

    private lateinit var teamRepo: TeamRepository
    private lateinit var imageRepo: ImageRepository
    private lateinit var categoryRepo: CategoryRepository
    private lateinit var leagueRepo: LeageRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = getString(R.string.gestion_equipos)

        teamRepo = TeamRepository()
        imageRepo = ImageRepository()
        categoryRepo = CategoryRepository()
        leagueRepo = LeageRepository()

        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        //insertMockData()

        //Obtenemos el nombre de las categorias para mostrarlo
        lifecycleScope.launch {
            try {
                categoriasList = categoryRepo.getAllCategories()
                categoriasNameList = categoriasList.map { it.categoryName.toString() }
                //Obtenemos el nombre de las ligas para mostrarlo
                ligasList = leagueRepo.getAllLeagues()
                ligasNameList = ligasList.map { it.leagueName.toString() }
            } catch (getE: GetAllError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
        }


        binding.btnAlta.setOnClickListener() {
            showEquiposDialog(null, "alta")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            lifecycleScope.launch {
                requireContext().contentResolver.openInputStream(imageUri!!).use { inputStream ->
                    val mimeType = requireContext().contentResolver.getType(imageUri)
                    val extension = mimeType?.substringAfterLast("/")
                    val imageFile = File.createTempFile("image", ".$extension")
                    FileOutputStream(imageFile).use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                    val metadata = ImageMetadata("team_logo", null, false)
                    val imageId = imageRepo.uploadImage(imageFile, metadata)
                    imageString = imageId
                }
            }
        }
    }

    private fun showEquiposDialog(selectedEquipo: Pair<Int?, String?>?, modo: String) {
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
        val btn_shield = dialogView.findViewById<Button>(R.id.btn_shield)

        var equipo: kkEquiposEntity? = null
        var equipoCategorySelected: String? = null
        var equipoLigaSelected: String? = null
        var dialogTitle = "Alta de Equipo"
        var allTeams: List<Team>
        var team: Team = Team(0, "Nombre", "Estadio", 0, 0, "Logo", false, false)

        // Adaptador para desplegables de categoria y liga
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasNameList)
        equipoCategory.setAdapter(adapter)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ligasNameList)
        equipoLeague.setAdapter(adapter)


        if(modo == "modificacion" && selectedEquipo!=null){//no es alta, es MODIFICACION
            lifecycleScope.launch {
                try {
                    allTeams = teamRepo.getAllTeams()
                    allTeams.forEach { t ->
                        if (t.teamId == selectedEquipo.first) {
                            team = t
                            equipoName.setText(team.teamName)
                            equipoLocation.setText(team.stadium)
                            check_isUnkina.isChecked = team.isTeamUnkina == true
                            check_visible.isChecked = team.picturesConsent == true
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

        equipoCategory.setOnItemClickListener { parent, view, position, id ->
            equipoCategorySelected = parent.getItemAtPosition(position).toString()
            val categoria = categoriasList.find { it.categoryName == equipoCategorySelected }
            equipoCategorySelected = categoria?.categoryId.toString()
        }
        equipoLeague.setOnItemClickListener { parent, view, position, id ->
            equipoLigaSelected = parent.getItemAtPosition(position).toString()
            val liga = ligasList.find { it.leagueName == equipoLigaSelected }
            equipoLigaSelected = liga?.leagueId.toString()
        }

        btn_shield.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (equipoName.text.toString().trim().isEmpty()) {
                equipoNameLayout.error = "Escribe un nombre"
                equipoNameLayout.requestFocus()
                allFieldsFilled = false
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
            if (imageString == null) {
                allFieldsFilled = false
            }

            if(allFieldsFilled){
                if (modo == "alta") {
                    team = Team(teamId = 0, teamName = equipoName.text.toString(), stadium = equipoLocation.text.toString(), categoryId =  equipoCategorySelected?.toInt(), leagueId =  equipoLigaSelected?.toInt(), logo =  imageString, isTeamUnkina =  check_isUnkina.isChecked, picturesConsent =  check_visible.isChecked)
                    lifecycleScope.launch {
                        try {
                            teamRepo.create(team)
                        } catch (loginE: LoginError) {
                            // Mostrar mensaje de error sobre problemas con la autenticación o permisos
                        } catch (createE: CreateError) {
                            // Mostrar mensaje de error sobre problemas generales durante la creación
                        }

                    }
                    //database.kkequipostDao.insert(kkEquiposEntity(name =  equipoName.text.toString(), campo =  equipoLocation.text.toString(), categoria =  equipoCategorySelected?.toInt(), liga =  equipoLigaSelected?.toInt(), escudo =  imageString, isUnkina =  check_isUnkina.isChecked, visible =  check_visible.isChecked))
                } else {
                    lifecycleScope.launch {
                        try {
                        team.teamName = equipoName.text.toString()
                        team.stadium = equipoLocation.text.toString()
                        team.categoryId = equipoCategorySelected?.toInt()
                        team.leagueId = equipoLigaSelected?.toInt()
                        team.logo = "EscudoNew"
                        team.isTeamUnkina = check_isUnkina.isChecked
                        team.picturesConsent = check_visible.isChecked
                        teamRepo.update(team)
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

    private fun showModEquiposDialog(){
        search(requireContext()){selectedEquipo ->
            selectedEquipo?.let {
                showEquiposDialog(selectedEquipo, "modificacion")
            }
        }
    }

    private fun showBajaEquiposDialog(onEquiposSelected: ( Pair<Int?, String?>) -> Unit) {
        search(requireContext()) { equipoSelected ->
            equipoSelected?.let { selectedEquipo ->
                selectedEquipo?.let {
                    onEquiposSelected(it)
                }
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedEquipo:  Pair<Int?, String?>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar el equipo?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val allCategories = teamRepo.getAllTeams()
                        allCategories.forEach { t ->
                            if (t.teamId == selectedEquipo.first) {
                                teamRepo.delete(t)
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

        tv_name.text = selectedEquipo.second
    }


    private fun convertirImagenABase64(imageUri: Uri): String {
        val inputStream = requireActivity().contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun convertirBase64AImageView(base64String: String): Bitmap? {
        if (base64String.isNotEmpty()) {
            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
        return null
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
                val lista = teamRepo.getAllTeams()
                listNames = lista.map { Pair(it.teamId, it.teamName) }
                title = "Modificación de Equipo"
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


        var list: List<Team> = listOf()
        lifecycleScope.launch {
            try {
                list = teamRepo.getAllTeams()
            } catch (createE: CreateError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
            filteredList = list.filter {
                it.teamName!!.contains(query, ignoreCase = true) ||
                        it.teamName!!.contains(query, ignoreCase = true)
            }.map { Pair(it.categoryId, it.teamName) } as List<Pair<Int, String>>
        }
        listAdapter.clear()
        listAdapter.addAll(filteredList)
        listAdapter.notifyDataSetChanged()
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
            kkFotosEntity(title= "img_9116", temporada = "22-23", equipoId = 2, galeria = false),
            kkFotosEntity( title = "img_9130", temporada = "23-24", equipoId = null, galeria = true),
            kkFotosEntity(title = "img_9137", temporada = "22-23", equipoId = null, galeria = true),
            kkFotosEntity(title = "img_9114", temporada = "23-24", equipoId = 2, galeria = false),
            kkFotosEntity(title = "img_9362", temporada = "22-23", equipoId = 2, galeria = false),
            kkFotosEntity(title = "img_9140", temporada = "24-25", equipoId = null, galeria = true),
            kkFotosEntity(title = "img_9154", temporada = "22-23", equipoId = 2, galeria = false),
            kkFotosEntity(title = "img_9159", temporada = "22-23", equipoId = 2, galeria = false),
        )
        fotos.forEach { database.kkfotosDao.insert(it) }


        val users = listOf(
            kkUsersEntity(foto= null, nombre = "Ainhoa", apellido = "Lopez", mail = "ainhoa@mail.com", password = "xxx", fecha_nacimiento = "28-09-1999", equipoId = 2, ocupacionId = 2, admin = true, activo = true),
            kkUsersEntity(foto= "luna", nombre = "Mario", apellido = "Gomez", mail = "mario@mail.com", password = "xxx", fecha_nacimiento = "15-12-2001", equipoId = 2, ocupacionId = 1, admin = true, activo = true),
            kkUsersEntity(foto= "img_9116", nombre = "Sergio", apellido = "Perez", mail = "sergio@mail.com", password = "xxx", fecha_nacimiento = "15-12-2001", equipoId = 2, ocupacionId = 1, admin = true, activo = true),

        )
        users.forEach { database.kkUsersDao.insert(it) }

    }
}
