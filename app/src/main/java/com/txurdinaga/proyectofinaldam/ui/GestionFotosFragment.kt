package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.data.model.ImageMetadata
import com.txurdinaga.proyectofinaldam.data.model.Team
import com.txurdinaga.proyectofinaldam.data.repo.ImageRepository
import com.txurdinaga.proyectofinaldam.data.repo.TeamRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.SearchList
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class GestionFotosFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var imageRepo: ImageRepository
    private var imageString: String=""


    var equipoSeleccionado = 0
    var equipoSelectedId: Int = -1

    private lateinit var equiposList: List<Team>
    private lateinit var teamRepo: TeamRepository

    private lateinit var metadata: ImageMetadata
    private var imageId: String = ""
    private lateinit var imageFile: File


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = getString(R.string.gestion_fotos)

        imageRepo = ImageRepository()
        teamRepo = TeamRepository()


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
        //binding.btnModificacion.setOnClickListener() {
        //    showModFotosDialog()
        //}

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
                    imageFile = File.createTempFile("image", ".$extension")
                    FileOutputStream(imageFile).use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                }
            }
        }
    }

    private fun showFotosDialog(selectedFotos: Pair<Int, String>?, modo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gestion_foto, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var foto: kkFotosEntity? = null
        val fotoName = dialogView.findViewById<EditText>(R.id.name)
        val fotoNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Fotos"
        val equipo = dialogView.findViewById<AutoCompleteTextView>(R.id.equipo)
        var btn_shield = dialogView.findViewById<Button>(R.id.foto)
        var equiposNameList= mutableListOf<Team>()

        lifecycleScope.launch {
            try {
                equiposList = teamRepo.getAllTeams()
                equiposList.forEach { t ->
                    equiposNameList.add(t)
                }
            } catch (getE: GetAllError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
        }

        val adapterEquipos = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, equiposNameList)
        equipo.setAdapter(adapterEquipos)

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

        equipo.setOnItemClickListener { parent, view, position, id ->
            var equipoSelected = parent.getItemAtPosition(position) as Team
            equipoSelectedId = equipoSelected.teamId

        }

        btn_shield.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


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
                    lifecycleScope.launch {
                        try {
                            metadata = ImageMetadata("team_image", equipoSelectedId, true)
                            imageId = imageRepo.uploadImage(imageFile, metadata)
                            imageString = imageId
                        } catch (getE: GetAllError) {
                            // Mostrar mensaje de error sobre problemas generales durante la creación
                        }
                    }


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