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
import android.widget.AutoCompleteTextView
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
import com.google.android.material.textfield.TextInputLayout
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
    var equipoSelectedId: Int = -1
    var ocupacionSelectedId: Int = -1



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
        if (cardData.title!=null){
            showDialog(cardData, position)
        }
    }

    private fun showDialog(cardData: CardData?, position: Int?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.card_edit_personal, null)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogView)


        var fecha = dialogView.findViewById<TextView>(R.id.textViewFecha)
            .apply {
            setOnClickListener { showDatePickerDialog(this) }
        }

        var name = dialogView.findViewById<TextInputEditText>(R.id.textViewNombre)
        var apellido = dialogView.findViewById<TextInputEditText>(R.id.textViewApellidos)
        var img = dialogView.findViewById<ImageView>(R.id.foto)
        var email = dialogView.findViewById<TextInputEditText>(R.id.email)
        var isAdmin = dialogView.findViewById<CheckBox>(R.id.checkBoxAdmin)
        var isActiva = dialogView.findViewById<CheckBox>(R.id.checkBoxActiva)
        val equipo = dialogView.findViewById<AutoCompleteTextView>(R.id.equipo)
        val ocupacion = dialogView.findViewById<AutoCompleteTextView>(R.id.ocupacion)

        val nombreLayout =dialogView.findViewById<TextInputLayout>(R.id.LayoutNombre)
        val apellidoLayout =dialogView.findViewById<TextInputLayout>(R.id.LayoutApellidos)
        val mailLayout =dialogView.findViewById<TextInputLayout>(R.id.emailLayout)
        val fechaLayout =dialogView.findViewById<TextInputLayout>(R.id.LayoutFecha)
        val equipoLayout =dialogView.findViewById<TextInputLayout>(R.id.spEquipo)
        val ocupacionLayout =dialogView.findViewById<TextInputLayout>(R.id.spOcupacion)



        var btnBorrar = dialogView.findViewById<ImageView>(R.id.imageViewCancelar)
        var btnConfirmar = dialogView.findViewById<ImageView>(R.id.imageViewConfirmar)



        val ListEquiposDialog = mutableListOf<kkEquiposEntity>()
        val equiposVisibles =database.kkequipostDao.getVisibleEquipos()
        equiposVisibles.forEach { equipoVisible ->
            ListEquiposDialog.add(equipoVisible)
        }
        val adapterEquipos = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ListEquiposDialog)
        equipo.setAdapter(adapterEquipos)


        val ListOcupaciones = mutableListOf<kkOcupacionesEntity>()
        val ocupacionesDialog =database.kkOcupacionesDao.getAllOcupaciones()
        ocupacionesDialog.forEach { ocupacionDialog ->
            ListOcupaciones.add(ocupacionDialog)
        }
        val adapterOcupacion = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ListOcupaciones)
        ocupacion.setAdapter(adapterOcupacion)



        val dialog = builder.create()
        dialog.show()



        equipo.setOnItemClickListener { parent, view, position, id ->
            var equipoSelected = parent.getItemAtPosition(position) as kkEquiposEntity
            equipoSelectedId = equipoSelected.id

        }
        ocupacion.setOnItemClickListener { parent, view, position, id ->
            var ocupacionSelected = parent.getItemAtPosition(position) as kkOcupacionesEntity
            ocupacionSelectedId = ocupacionSelected.id
        }

        fun comprobarCampos():Boolean{
            var allFieldsFilled = true

            if (name.text.toString().isEmpty()) {
                nombreLayout.error = "Escribe un nombre"
                nombreLayout.requestFocus()
                allFieldsFilled = false
            }

            if (apellido.text.toString().isEmpty()) {
                apellidoLayout.error = "Escribe un apellido"
                apellidoLayout.requestFocus()
                allFieldsFilled = false
            }

            if (fecha.text.toString().isEmpty()) {
                fechaLayout.error = "Escribe una fecha"
                fechaLayout.requestFocus()
                allFieldsFilled = false
            }

            if (email.text.toString().isEmpty()) {
                mailLayout.error = "Escribe un email"
                mailLayout.requestFocus()
                allFieldsFilled = false
            }

            if (equipoSelectedId==-1) {
                equipoLayout.error = "Selecciona un equipo"
                equipoLayout.requestFocus()
                allFieldsFilled = false
            }

            if (ocupacionSelectedId==-1) {
                ocupacionLayout.error = "Selecciona una ocupacion"
                ocupacionLayout.requestFocus()
                allFieldsFilled = false
            }
            return allFieldsFilled
        }


        if (cardData!= null && position!=null){
            if (cardData.id !=null){
                val user = database.kkUsersDao.getUsersById(cardData.id)

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

                btnBorrar.setOnClickListener {
                    borrarUser(cardData.id)
                    dialog.dismiss()

                }
                btnConfirmar.setOnClickListener {
                    val todoOk = comprobarCampos()
                    if(todoOk){
                        database.kkUsersDao.update(kkUsersEntity(id = user.id, foto = null, nombre = name.text.toString(), apellido = apellido.text.toString(), mail = email.text.toString(), password = "xxx", fecha_nacimiento = fecha.text.toString(), equipoId = equipoSelectedId, ocupacionId = ocupacionSelectedId, admin = isAdmin.isChecked, activo = isActiva.isChecked ))
                        dialog.dismiss()
                    }
                }
            }
        } else{
            img.setImageResource(resources.getIdentifier("avatar", "drawable", requireContext().packageName))
            btnBorrar.setOnClickListener {
                dialog.dismiss()
            }
            btnConfirmar.setOnClickListener {
                btnConfirmar.setOnClickListener {
                    val todoOk = comprobarCampos()
                    if(todoOk){
                        database.kkUsersDao.insert(kkUsersEntity(foto = null, nombre = name.text.toString(), apellido = apellido.text.toString(), mail = email.text.toString(), password = "xxx", fecha_nacimiento = fecha.text.toString(), equipoId = equipoSelectedId, ocupacionId = ocupacionSelectedId, admin = isAdmin.isChecked, activo = isActiva.isChecked ))
                        dialog.dismiss()

                    }
                }
            }
        }



    }


    private fun borrarUser(id:Int){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)
        var user = database.kkUsersDao.getUsersById(id)


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar el usuario?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                database.kkUsersDao.delete(user)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = user.nombre +" "+ user.apellido

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
