package com.txurdinaga.proyectofinaldam.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentFotosBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        val addPlayer = binding.add
        addPlayer.visibility = View.VISIBLE

        addPlayer.setOnClickListener {
            showDialog(null, null)
        }

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

                    usersByEquipo.forEach { user ->
                        var ocupacion = database.kkOcupacionesDao.getOcupacionById(user.ocupacionId)

                        if (user.ocupacionId == 1){
                            datasetJugadores.add(
                                CardData(
                                    user.foto,
                                    user.id,
                                    user.nombre,
                                    ocupacion.name

                                )
                            )
                        } else{
                            datasetEquipoTecnico.add(
                                CardData(
                                    user.foto,
                                    user.id,
                                    user.nombre,
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
        showDialog(cardData, position)
    }

    private fun showDialog(cardData: CardData?, position: Int?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.card_edit_personal, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)


        var imageViewDate = dialogView.findViewById<TextView>(R.id.textViewFecha)
            .apply {
            setOnClickListener { showDatePickerDialog(this) }
        }

        if (cardData!= null && position!=null){
            if (cardData.id !=null){
                val user = database.kkUsersDao.getUsersById(cardData.id)
                var name = dialogView.findViewById<EditText>(R.id.textViewNombre)
                var apellido = dialogView.findViewById<EditText>(R.id.textViewApellidos)
                var img = dialogView.findViewById<ImageView>(R.id.foto)
                var fecha = dialogView.findViewById<EditText>(R.id.textViewFecha)
                var email = dialogView.findViewById<TextInputEditText>(R.id.email)
                var isAdmin = dialogView.findViewById<CheckBox>(R.id.checkBoxAdmin)
                var isActiva = dialogView.findViewById<CheckBox>(R.id.checkBoxActiva)

                name.setText(user.nombre)
                apellido.setText(user.apellido)
                img.setImageResource(resources.getIdentifier(user.foto, "drawable", requireContext().packageName))
                fecha.setText(user.fecha_nacimiento)
                email.setText(user.mail)
                if (user.admin){
                    isAdmin.isChecked= true
                }
                if (user.activo){
                    isActiva.isChecked = true
                }

            }
        }

        val dialog = builder.create()
        dialog.show()

    }

    private fun showDatePickerDialog(txtInsertarFecha: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Establecer el idioma de toda la aplicación en español
        Locale.setDefault(Locale("es", "ES"))

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthOfYear, dayOfMonth ->
                // Formatear la fecha seleccionada como desees
                val fechaSeleccionada = "$dayOfMonth/${monthOfYear + 1}/$yearSelected"
                txtInsertarFecha.text = fechaSeleccionada
            },
            year,
            month,
            day
        )

        // Configurar el primer día de la semana en domingo
        datePickerDialog.datePicker.firstDayOfWeek = Calendar.SUNDAY

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onDestroyView()
        _binding = null
    }


}
