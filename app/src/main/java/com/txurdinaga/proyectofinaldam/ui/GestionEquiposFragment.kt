package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.SearchList
import java.io.ByteArrayOutputStream


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
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageString: String

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
        //insertMockData()

        //Obtenemos el nombre de las categorias para mostrarlo
        categoriasList = database.kkcategoryDao.getAllCategorias()
        categoriasNameList = categoriasList.map { it.name }
        //Obtenemos el nombre de las ligas para mostrarlo
        ligasList = database.kkligasDao.getAllLigas()
        ligasNameList = ligasList.map { it.name }



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
            imageString = convertirImagenABase64(imageUri!!)
        }
    }

    private fun showEquiposDialog(selectedEquipo:  Pair<Int, String>?, modo: String) {
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

        // Adaptador para desplegables de categoria y liga
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasNameList)
        equipoCategory.setAdapter(adapter)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ligasNameList)
        equipoLeague.setAdapter(adapter)


        if(modo == "modificacion" && selectedEquipo!=null){//no es alta, es MODIFICACION
            equipo = database.kkequipostDao.getEquiposById(selectedEquipo.first)
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
            }/*else{//comprobar que esta nombre no este guardado ya
                val estaEquipo = database.kkequipostDao.getEquiposByName(equipoName.text.toString())
                if(estaEquipo != null && selectedEquipo == "alta"){
                    equipoNameLayout.error = "Ya existe este equipo"
                    equipoNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }*/

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
                    database.kkequipostDao.insert(kkEquiposEntity(name =  equipoName.text.toString(), campo =  equipoLocation.text.toString(), categoria =  equipoCategorySelected?.toInt(), liga =  equipoLigaSelected?.toInt(), escudo =  imageString, isUnkina =  check_isUnkina.isChecked, visible =  check_visible.isChecked))
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
                showEquiposDialog(selectedEquipo, "modificacion")
            }
        }
    }

    private fun showBajaEquiposDialog(onEquiposSelected: ( Pair<Int, String>) -> Unit) {
        searchList.search(requireContext(), database, "equipo") { ligasSelected ->
            ligasSelected?.let { selectedEquipo ->
                selectedEquipo?.let {
                    onEquiposSelected(it)
                }
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedEquipo:  Pair<Int, String>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar el equipo?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val equipo = database.kkequipostDao.getEquiposById(selectedEquipo.first)
                database.kkequipostDao.delete(equipo)
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
            kkUsersEntity(foto= "sol", nombre = "Ainhoa", apellido = "Lopez", mail = "ainhoa@mail.com", password = "xxx", fecha_nacimiento = "28-09-1999", equipoId = 2, ocupacionId = 2, admin = true, activo = true),
            kkUsersEntity(foto= "luna", nombre = "Mario", apellido = "Gomez", mail = "mario@mail.com", password = "xxx", fecha_nacimiento = "15-12-2001", equipoId = 2, ocupacionId = 1, admin = true, activo = true),
            kkUsersEntity(foto= "img_9116", nombre = "Sergio", apellido = "Perez", mail = "sergio@mail.com", password = "xxx", fecha_nacimiento = "15-12-2001", equipoId = 2, ocupacionId = 1, admin = true, activo = true),

        )
        users.forEach { database.kkUsersDao.insert(it) }

    }
}
