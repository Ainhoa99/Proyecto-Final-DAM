package com.txurdinaga.proyectofinaldam.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import java.io.ByteArrayOutputStream


class GestionPatrocinadoresFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageString: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = "Gestión de Patrocinadores"


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showPatrocinadoresDialog("alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaPatrocinadoresDialog { selectedPatrocinadore ->
                showConfirmDeleteDialog(selectedPatrocinadore)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModPatrocinadoresDialog()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            //Imagen.visibility = ImageView.VISIBLE
            //Imagen.setImageURI(imageUri)

            imageString = convertirImagenABase64(imageUri!!)
        }

    }

    private fun showPatrocinadoresDialog(selectedPatrocinador: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gestion_patrocinador, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var patrocinador: kkPatrocinadoresEntity? = null
        val patrocinadorName = dialogView.findViewById<EditText>(R.id.name)
        val patrocinadorNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        val btn_picture = dialogView.findViewById<Button>(R.id.btn_picture)
        val patrocinadorMoney = dialogView.findViewById<EditText>(R.id.money)
        val patrocinadorMoneyLayout = dialogView.findViewById<TextInputLayout>(R.id.moneyLayout)
        val check_isPatrocinador = dialogView.findViewById<CheckBox>(R.id.check_isPatrocinador)
        val check_activo = dialogView.findViewById<CheckBox>(R.id.check_activo)
        var dialogTitle = "Alta de Patrocinadores"


        if(selectedPatrocinador != "alta"){//no es alta, es MODIFICACION
            patrocinador = database.kkPatrocinadoresDao.getTeacherByName(selectedPatrocinador)
            dialogTitle = "Modificación de Patrocinadores"

            patrocinadorName.setText(patrocinador.name)
            patrocinadorMoney.setText(patrocinador.dinero.toString())
            check_activo.isChecked = patrocinador.activo
            check_isPatrocinador.isChecked = patrocinador.isPatrocinador
        }

        builder.setTitle(dialogTitle)
        builder.setPositiveButton("Insertar", null)
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()


        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            var allFieldsFilled = true

            if (patrocinadorName.text.toString().trim().isEmpty()) {
                patrocinadorNameLayout.error = "Escribe un nombre"
                patrocinadorNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                val estaPatrocinador = database.kkPatrocinadoresDao.getTeacherByName(patrocinadorName.text.toString())
                if(estaPatrocinador != null && selectedPatrocinador == "alta"){
                    patrocinadorNameLayout.error = "Ya existe esta patrocinador"
                    patrocinadorNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }


            if(allFieldsFilled){
                if (selectedPatrocinador == "alta") {
                    database.kkPatrocinadoresDao.insert(kkPatrocinadoresEntity(0, patrocinadorName.text.toString(), imageString, check_isPatrocinador.isChecked, check_activo.isChecked, patrocinadorMoney.text.toString().toDouble()))
                } else {
                    if (patrocinador != null) {
                        patrocinador.name = patrocinadorName.text.toString()
                        database.kkPatrocinadoresDao.update(patrocinador)
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModPatrocinadoresDialog(){
        searchList.search(requireContext(), database, "patrocinador"){ patrocinadorSelected ->
            patrocinadorSelected?.let {
                showPatrocinadoresDialog(patrocinadorSelected)
            }
        }
    }

    private fun showBajaPatrocinadoresDialog(onPatrocinadorSelected: (String) -> Unit) {
        searchList.search(requireContext(), database, "patrocinador") { patrocinadorSelected ->
            patrocinadorSelected?.let {
                onPatrocinadorSelected(it)
            }
        }
    }


    private fun showConfirmDeleteDialog(selectedPatrocinador: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la patrocinador?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val patrocinador = database.kkPatrocinadoresDao.getTeacherByName(selectedPatrocinador)
                database.kkPatrocinadoresDao.delete(patrocinador)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedPatrocinador
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
}