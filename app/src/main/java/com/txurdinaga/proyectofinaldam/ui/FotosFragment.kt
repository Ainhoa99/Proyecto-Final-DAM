package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
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


        if(fotos.isEmpty()){
            val foto1 = kkFotosEntity(title= "img_9116", temporada = "22-23", equipoId = 1, galeria = false)
            val foto2 = kkFotosEntity( title = "img_9130", temporada = "23-24", equipoId = 1, galeria = false)
            val foto3 = kkFotosEntity(title = "img_9137", temporada = "22-23", equipoId = 2, galeria = false)
            val foto4 = kkFotosEntity(title = "img_9114", temporada = "23-24", equipoId = 3, galeria = false)
            val foto5 = kkFotosEntity(title = "img_9362", temporada = "22-23", equipoId = 2, galeria = false)
            val foto6 = kkFotosEntity(title = "img_9140", temporada = "24-25", equipoId = 2, galeria = false)
            val foto7 = kkFotosEntity(title = "img_9154", temporada = "22-23", equipoId = 3, galeria = false)
            val foto8 = kkFotosEntity(title = "img_9159", temporada = "22-23", equipoId = 2, galeria = false)

            database.kkfotosDao.insert(foto2)
            database.kkfotosDao.insert(foto1)
            database.kkfotosDao.insert(foto3)
            database.kkfotosDao.insert(foto4)
            database.kkfotosDao.insert(foto5)
            database.kkfotosDao.insert(foto6)
            database.kkfotosDao.insert(foto7)
            database.kkfotosDao.insert(foto8)

            fotos = database.kkfotosDao.getAllFotos()
        }

        var temporadas = database.kkfotosDao.getTemporadas()


        if (fotos.isNotEmpty()){
            val ListTemporadas = mutableListOf<String>()
            temporadas.forEach { temporada ->
                ListTemporadas.add(temporada)
            }
            val spTemporadas = binding.temporadas
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ListTemporadas)
            spTemporadas.setAdapter(adapter)


        }

        if (fotos.isNotEmpty()){
            val dataset = mutableListOf<CardData>()
            fotos.forEach { foto ->
                dataset.add(
                    CardData(
                        foto.title
                    )
                )
            }


            val cardAdapter = CardAdapter(dataset, this)
            val recyclerView: RecyclerView = binding.recyclerView
            recyclerView.adapter = cardAdapter
            recyclerView.setHasFixedSize(true);
            recyclerView.isNestedScrollingEnabled = false;
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

        val fotos = database.kkfotosDao.getAllFotos()

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