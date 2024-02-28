package com.txurdinaga.proyectofinaldam.ui

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentFotosBinding

class PersonalFragment : Fragment(), ICardClickListener {
    private var _binding: FragmentFotosBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase

    var equipoSeleccionado = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFotosBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        database = Room.databaseBuilder(
            view.context, kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME)
            .allowMainThreadQueries()
            .build()

        var users = database.kkUsersDao.getAllUsers()

        var equipos = database.kkequipostDao.getVisibleEquipos()



        if (users.isNotEmpty()){
            val ListEquipos = mutableListOf<kkEquiposEntity>()
            equipos.forEach { equipo ->
                ListEquipos.add(equipo)
            }
            val spEquipos = binding.spinner
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ListEquipos)
            Handler(Looper.getMainLooper()).postDelayed({
                spEquipos.setAdapter(adapter)
                binding.spinner.setText("Selecciona un equipo", false)
                spEquipos.setAdapter(adapter)

            }, 100)

            spEquipos.setOnItemClickListener { parent, view, position, id ->
                val msgFotosEmpty = binding.msgFotosEmpty
                val msgSelecciona = binding.msgSelecciona
                val msgJugadores = binding.msgJugadores
                val msgEqTecnico = binding.msgEqTecnico

                msgSelecciona.visibility = View.GONE

                val selectedItem = parent.getItemAtPosition(position) as kkEquiposEntity
                // Aquí puedes hacer lo que necesites con el elemento seleccionado
                // Por ejemplo, mostrar un Toast con el elemento seleccionado
                Toast.makeText(requireContext(), "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
                equipoSeleccionado = selectedItem.id
                val usersByEquipo = database.kkUsersDao.getUsersByEquipo(equipoSeleccionado)

                if (usersByEquipo.isNotEmpty()){
                    msgFotosEmpty.visibility = View.GONE
                    msgJugadores.visibility = View.VISIBLE
                    msgEqTecnico.visibility = View.VISIBLE
                    val rvEquipoTecnico = binding.recyclerViewEquipoTecnico
                    rvEquipoTecnico.visibility = View.VISIBLE
                    val datasetJugadores = mutableListOf<CardData>()
                    val datasetEquipoTecnico = mutableListOf<CardData>()

                    usersByEquipo.forEach { fotoEquipo ->
                        var ocupacion = database.kkOcupacionesDao.getOcupacionById(fotoEquipo.id)

                        if (fotoEquipo.ocupacionId == 1){
                            datasetJugadores.add(
                                CardData(
                                    fotoEquipo.foto,
                                    fotoEquipo.nombre,
                                    ocupacion.name
                                )
                            )
                        } else{
                            datasetEquipoTecnico.add(
                                CardData(
                                    fotoEquipo.foto,
                                    fotoEquipo.nombre,
                                    ocupacion.name
                                )
                            )
                        }
                    }


                    val cardAdapter = CardAdapter(datasetJugadores, this)
                    val recyclerView: RecyclerView = binding.recyclerView
                    recyclerView.adapter = cardAdapter
                    recyclerView.setHasFixedSize(true);
                    recyclerView.isNestedScrollingEnabled = false;
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                    // Verificamos si el layoutManager no es nulo para evitar errores
                    layoutManager?.apply {
                        // Establecemos el spanCount deseado
                        spanCount = 3
                    }

                    val cardAdapterEquipoTecnico = CardAdapter(datasetEquipoTecnico, this)
                    val recyclerViewEquipoTecnico: RecyclerView = binding.recyclerViewEquipoTecnico
                    recyclerViewEquipoTecnico.adapter = cardAdapterEquipoTecnico
                    recyclerViewEquipoTecnico.setHasFixedSize(true);
                    recyclerViewEquipoTecnico.isNestedScrollingEnabled = false;
                } else{
                    msgFotosEmpty.visibility = View.VISIBLE
                    msgJugadores.visibility = View.GONE
                    msgEqTecnico.visibility = View.GONE
                    val emptyAdapter = CardAdapter(emptyList(), this)
                    val recyclerView: RecyclerView = binding.recyclerView
                    recyclerView.adapter = emptyAdapter
                    val recyclerViewEquipoTecnico: RecyclerView = binding.recyclerViewEquipoTecnico
                    recyclerViewEquipoTecnico.adapter = emptyAdapter
                }

            }

        }

    }


    override fun onCardClick(position: Int, cardData: CardData) {
        TODO("Not yet implemented")
    }


    override fun onDestroyView() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onDestroyView()
        _binding = null
    }


}
