package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionBinding
import com.txurdinaga.proyectofinaldam.ui.util.SearchList


class GestionLigasFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var ligasListAdapter: ArrayAdapter<String>
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

        binding.tvTitle.text = "Gestión de Ligas"


        //creamos la bbdd
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


        binding.btnAlta.setOnClickListener() {
            showEquiposDialog("alta")
        }
        binding.btnBaja.setOnClickListener() {
            showBajaEquiposDialog { selectedLiga ->
                showConfirmDeleteDialog(selectedLiga)
            }
        }
        binding.btnModificacion.setOnClickListener() {
            showModEquiposDialog()
        }

        return binding.root
    }

    private fun showEquiposDialog(selectedLiga: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_name, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)

        var liga: kkLigasEntity? = null
        val ligaName = dialogView.findViewById<EditText>(R.id.name)
        val equipoNameLayout = dialogView.findViewById<TextInputLayout>(R.id.nameLayout)
        var dialogTitle = "Alta de Liga"


        if(selectedLiga != "alta"){//no es alta, es MODIFICACION
            liga = database.kkligasDao.getTeacherByName(selectedLiga)
            dialogTitle = "Modificación de Liga"

            ligaName.setText(liga.name)
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
                equipoNameLayout.error = "Escribe un nombre"
                equipoNameLayout.requestFocus()
                allFieldsFilled = false
            }else{//comprobar que esta nombre no este guardado ya
                val estaLiga = database.kkligasDao.getTeacherByName(ligaName.text.toString())
                if(estaLiga != null && selectedLiga == "alta"){
                    equipoNameLayout.error = "Ya existe esta liga"
                    equipoNameLayout.requestFocus()
                    allFieldsFilled = false
                }
            }

            if(allFieldsFilled){
                if (selectedLiga == "alta") {
                    database.kkligasDao.insert(kkLigasEntity(0, ligaName.text.toString()))
                } else {
                    if (liga != null) {
                        liga.name = ligaName.text.toString()
                        database.kkligasDao.update(liga)
                    }
                }
                dialog.dismiss()
            }
        }
    }

    private fun showModEquiposDialog(){
        searchList.search(requireContext(), database, "equipo"){ ligasSelected ->
            ligasSelected?.let {
                showEquiposDialog(ligasSelected)
            }
        }
    }

    private fun showBajaEquiposDialog(onLigasSelected: (String) -> Unit) {
        searchList.search(requireContext(), database, "equipo") { ligasSelected ->
            ligasSelected?.let {
                onLigasSelected(it)
            }
        }
    }


     private fun showConfirmDeleteDialog(selectedLiga: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar la liga?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                val liga = database.kkligasDao.getTeacherByName(selectedLiga)
                database.kkligasDao.delete(liga)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = selectedLiga
    }
}