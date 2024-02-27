package com.txurdinaga.proyectofinaldam.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.ui.kkAppDatabase

class SearchList(context: Context?) {
    private lateinit var listAdapter: ArrayAdapter<String>

    fun search(
        context: Context,
        database: kkAppDatabase,
        whereIam: String,
        onLigasSelected: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_buscador, null)
        val lv_List = dialogView.findViewById<ListView>(R.id.lv_List)
        val textInputSearch = dialogView.findViewById<TextInputEditText>(R.id.textInputSearch)

        var listNames: List<String> = listOf()
        var title = "Gestion"

        when(whereIam){
            "equipo" -> {
                val lista = database.kkequipostDao.getAllEquipos()
                listNames = lista.map { it.name }
                title = "Alta de Equipo"
            }
            "liga" -> {
                val lista = database.kkligasDao.getAllTeachers()
                listNames = lista.map { it.name }
                title = "Alta de Liga"
            }
            "categoria" -> {
                val lista = database.kkcategoryDao.getAllTeachers()
                listNames = lista.map { it.name }
                title = "Alta de Categoria"
            }
            "patrocinador" -> {
                val lista = database.kkPatrocinadoresDao.getAllTeachers()
                listNames = lista.map { it.name }
                title = "Alta de Patrocinador"
            }
        }
        listAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listNames)
        lv_List.adapter = listAdapter

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        textInputSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterLigas(s.toString(), database, whereIam)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        lv_List.setOnItemClickListener { _, _, position, _ ->
            val selectedTeacher = listAdapter.getItem(position)
            selectedTeacher?.let {
                onLigasSelected(it)
                dialog.dismiss()
            }
        }
    }
    fun filterLigas(query: String, database: kkAppDatabase, whereIam: String) {
        //Ahora mismo esta repetido en varias lista por problemas de tipos
        var filteredList: List<String> = mutableListOf()
        when(whereIam){
            "equipo" -> {
                var list = database.kkequipostDao.getAllEquipos()
                filteredList = list.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.name.contains(query, ignoreCase = true)
                }.map { it.name }
            }
            "liga" -> {

                var list = database.kkligasDao.getAllTeachers()
                filteredList = list.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.name.contains(query, ignoreCase = true)
                }.map { it.name }
            }
        }

        listAdapter.clear()
        listAdapter.addAll(filteredList)
        listAdapter.notifyDataSetChanged()
    }
}