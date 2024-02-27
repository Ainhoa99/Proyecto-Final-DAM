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
import com.txurdinaga.proyectofinaldam.ui.util.SearchList


class GestionColaboradoresFragment : Fragment() {
    private var _binding: FragmentGestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase
    val searchList = SearchList(context)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionBinding.inflate(layoutInflater, container, false)

        binding.tvTitle.text = "Gestión de Colaboradores"


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
        //Obtenemos el nombre de las ligas para mostrarlo
        ligasList = database.kkligasDao.getAllTeachers()
        ligasNameList = ligasList.map { it.name }

        //insertMockData()


        binding.btnAlta.setOnClickListener() {

        }
        binding.btnBaja.setOnClickListener() {

        }
        binding.btnModificacion.setOnClickListener() {

        }

        return binding.root
    }










}