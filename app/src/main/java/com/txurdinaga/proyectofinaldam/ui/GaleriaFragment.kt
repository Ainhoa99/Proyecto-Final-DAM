package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.data.model.ImageMetadata
import com.txurdinaga.proyectofinaldam.data.model.Role
import com.txurdinaga.proyectofinaldam.data.model.Team
import com.txurdinaga.proyectofinaldam.data.model.User
import com.txurdinaga.proyectofinaldam.data.repo.ImageRepository
import com.txurdinaga.proyectofinaldam.data.repo.RoleRepository
import com.txurdinaga.proyectofinaldam.data.repo.TeamRepository
import com.txurdinaga.proyectofinaldam.data.repo.UserRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentFotosBinding
import com.txurdinaga.proyectofinaldam.util.GetAllError
import kotlinx.coroutines.launch

// NO ESTÁ IMPLEMENTADO POR NO TENER EL ENDPOINT DE LAS TEMPORADAS. AHORA MISMO HACE LO MISMO QUE FOTOS
class GaleriaFragment : Fragment(), ICardClickListener {
    private var _binding: FragmentFotosBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: kkAppDatabase


    var equipoSeleccionado = 0
    var equipoSelectedId: Int = -1
    var ocupacionSelectedId: Int = -1

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var equiposList: List<Team>
    private lateinit var usuariosList: List<User>
    private lateinit var ocupacionesList: List<Role>
    private lateinit var imagesList: List<ImageMetadata>

    private var user: User = User(userId = "", token = "", picture = "", name = "", surname = "", email = "", password = "", dateOfBirth = 0, teamId = 0, roleId = 0, isAdmin = false, isActive = false, isFirstLogin = false, lastSeen = 0, familyId = "")

    private lateinit var teamRepo: TeamRepository
    private lateinit var userRepo: UserRepository
    private lateinit var imageRepo: ImageRepository
    private lateinit var roleRepo: RoleRepository

    private var imageString: String=""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFotosBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        teamRepo = TeamRepository()
        userRepo = UserRepository()
        roleRepo = RoleRepository()
        imageRepo = ImageRepository()

        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        database = Room.databaseBuilder(
            view.context, kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME)
            .allowMainThreadQueries()
            .build()

        //      var fotos = database.kkfotosDao.getAllFotos()

        //     var equipos = database.kkequipostDao.getVisibleEquipos()

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

        val spEquipos = binding.spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, equiposNameList)
        Handler(Looper.getMainLooper()).postDelayed({
            spEquipos.setAdapter(adapter)
            binding.spinner.setText("Selecciona un equipo", false)
            spEquipos.setAdapter(adapter)

        }, 100)

        spEquipos.setOnItemClickListener { parent, view, position, id ->
            val msgFotosEmpty = binding.msgFotosEmpty
            val msgSelecciona = binding.msgSelecciona

            msgSelecciona.visibility = View.GONE
            val selectedItem = parent.getItemAtPosition(position) as Team
            // Aquí puedes hacer lo que necesites con el elemento seleccionado
            // Por ejemplo, mostrar un Toast con el elemento seleccionado
            Toast.makeText(requireContext(), "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            equipoSeleccionado = selectedItem.teamId
            //       val fotosByEquipo = database.kkfotosDao.getFotosByEquipo(equipoSeleccionado)

            var fotosNameList= mutableListOf<ImageMetadata>()

            fun mostrarLista(){

                if (fotosNameList.isNotEmpty()){
                    msgFotosEmpty.visibility = View.GONE

                    val dataset = mutableListOf<CardData>()
                    fotosNameList.forEach { fotoEquipo ->
                        dataset.add(
                            CardData(
                                fotoEquipo.imageId,
                                fotoEquipo.imageId,
                                null,
                                null,
                                fotoEquipo.url
                            )
                        )
                    }


                    val cardAdapter = CardAdapter(requireContext(),dataset, this)
                    val recyclerView: RecyclerView = binding.recyclerView
                    recyclerView.adapter = cardAdapter
                    recyclerView.setHasFixedSize(true);
                    recyclerView.isNestedScrollingEnabled = false;
                } else{
                    msgFotosEmpty.visibility = View.VISIBLE
                    val emptyAdapter = CardAdapter(requireContext(),emptyList(), this)
                    val recyclerView: RecyclerView = binding.recyclerView
                    recyclerView.adapter = emptyAdapter
                }
            }


            lifecycleScope.launch {
                try {
                    imagesList = imageRepo.getAllImages()
                    imagesList.forEach { t ->
                        if (t.teamId==equipoSeleccionado){
                            fotosNameList.add(t)
                        }
                    }
                    mostrarLista()
                } catch (getE: GetAllError) {
                    // Mostrar mensaje de error sobre problemas generales durante la creación
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


        //val fotos = database.kkfotosDao.getFotosByEquipo(equipoSeleccionado)

        var fotos= mutableListOf<ImageMetadata>()
        val dataset = mutableListOf<CardData>()

        fun mostrarFotos(){
            if (fotos.isNotEmpty()) {
                val images = mutableListOf<Int>()

                fotos.forEach { foto ->
                    dataset.add(
                        CardData(
                            foto.imageId,
                            foto.imageId,
                            null,
                            null,
                            foto.url
                        )
                    )
                }

                val viewPager = dialogView.findViewById<ViewPager2>(R.id.viewPager)

                val adapter = ImageAdapter(requireContext(), dataset)
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
        lifecycleScope.launch {
            try {
                imagesList = imageRepo.getAllImages()
                imagesList.forEach { t ->
                    if (t.teamId==equipoSeleccionado){
                        fotos.add(t)
                        mostrarFotos()
                    }
                }
            } catch (getE: GetAllError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
        }



    }
    override fun onDestroyView() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onDestroyView()
        _binding = null
    }


}