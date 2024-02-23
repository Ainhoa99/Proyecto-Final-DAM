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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionEquiposBinding


class GestionEquiposFragment : Fragment() {
    private var _binding: FragmentGestionEquiposBinding? = null
    private val binding get() = _binding!!
    private lateinit var equiposListAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionEquiposBinding.inflate(layoutInflater, container, false)



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
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gestion_equipos, null)
        if(selectedEquipo != "alta"){
            val equipoName = dialogView.findViewById<EditText>(R.id.name)
            val equipoLocation = dialogView.findViewById<EditText>(R.id.location)
            val equipoCategory = dialogView.findViewById<AutoCompleteTextView>(R.id.category)
            val equipoLeague = dialogView.findViewById<AutoCompleteTextView>(R.id.league)

            //Aqui habra que hacer una llamada a la bbdd con el nombre del equipo para traer
            //sus datos y cambiarlo

            equipoName.setText(selectedEquipo)
            equipoLocation.setText("Campo")
            equipoCategory.setText("Categoria")
            equipoLeague.setText("Liga")

        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Alta de Equipo")
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.cancel() }
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

        val equipos = arrayOf("Barcelona", "Real Madrid", "Manchester United", "Bayern Munich", "Juventus")
        val teacherNames = equipos.map { it }
        equiposListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, teacherNames)
        lv_equiposList.adapter = equiposListAdapter

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Baja de Equipo")
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
        val equipos = arrayOf("Barcelona", "Real Madrid", "Manchester United", "Bayern Munich", "Juventus")
        val filteredList = equipos.filter {
            it.contains(query, ignoreCase = true) ||
                    it.contains(query, ignoreCase = true)
        }.map { it }

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
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.cancel() }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_equiponame.text = selectedEquipo
    }
}