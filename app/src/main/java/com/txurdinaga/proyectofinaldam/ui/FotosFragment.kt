package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentFotosBinding



class FotosFragment : Fragment(), ICardClickListener {
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

        var fotos = database.kkfotosDao.getAllFotos()

        var equipos = database.kkequipostDao.getVisibleEquipos()



        if (fotos.isNotEmpty()){
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

                msgSelecciona.visibility = View.GONE
                val selectedItem = parent.getItemAtPosition(position) as kkEquiposEntity
                // Aquí puedes hacer lo que necesites con el elemento seleccionado
                // Por ejemplo, mostrar un Toast con el elemento seleccionado
                Toast.makeText(requireContext(), "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
                equipoSeleccionado = selectedItem.id
                val fotosByEquipo = database.kkfotosDao.getFotosByEquipo(equipoSeleccionado)

                if (fotosByEquipo.isNotEmpty()){
                    msgFotosEmpty.visibility = View.GONE

                    val dataset = mutableListOf<CardData>()
                    fotosByEquipo.forEach { fotoEquipo ->
                        dataset.add(
                            CardData(
                                fotoEquipo.title,
                                null,
                                null,
                                null
                            )
                        )
                    }


                    val cardAdapter = CardAdapter(dataset, this)
                    val recyclerView: RecyclerView = binding.recyclerView
                    recyclerView.adapter = cardAdapter
                    recyclerView.setHasFixedSize(true);
                    recyclerView.isNestedScrollingEnabled = false;
                } else{
                    msgFotosEmpty.visibility = View.VISIBLE
                    val emptyAdapter = CardAdapter(emptyList(), this)
                    val recyclerView: RecyclerView = binding.recyclerView
                    recyclerView.adapter = emptyAdapter
                }

            }

        }

    }

    override fun onCardClick(position: Int, cardData: CardData) {
        showCarrousel(cardData, position)

    }

    private fun showCarrousel(cardData: CardData, position: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_carrousel, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME)
            .allowMainThreadQueries()
            .build()


        val fotos = database.kkfotosDao.getFotosByEquipo(equipoSeleccionado)

        if (fotos.isNotEmpty()) {
            val images = mutableListOf<Int>()

            fotos.forEach { foto ->
                val resourceId = resources.getIdentifier(foto.title, "drawable", requireContext().packageName)
                images.add(resourceId)
            }

            val viewPager = dialogView.findViewById<ViewPager2>(R.id.viewPager)

            val adapter = ImageAdapter(images)
            viewPager.adapter = adapter
            viewPager.setCurrentItem(position, false)

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewPager.post {
                        viewPager.setCurrentItem(position, false)
                    }
                }
            })

            // Posterga la llamada a dialog.show() después de un breve retraso
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.show()
            }, 10)



        }

    }
    override fun onDestroyView() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onDestroyView()
        _binding = null
    }


}