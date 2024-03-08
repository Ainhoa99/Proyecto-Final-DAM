package com.txurdinaga.proyectofinaldam.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
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
import com.txurdinaga.proyectofinaldam.data.model.Category
import com.txurdinaga.proyectofinaldam.data.model.Role
import com.txurdinaga.proyectofinaldam.data.model.Team
import com.txurdinaga.proyectofinaldam.data.model.User
import com.txurdinaga.proyectofinaldam.data.repo.RoleRepository
import com.txurdinaga.proyectofinaldam.data.repo.TeamRepository
import com.txurdinaga.proyectofinaldam.data.repo.UserRepository
import com.txurdinaga.proyectofinaldam.databinding.FragmentFotosBinding
import com.txurdinaga.proyectofinaldam.util.GetAllError
import kotlinx.coroutines.launch
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


    private lateinit var equiposList: List<Team>
    private lateinit var usuariosList: List<User>
    private lateinit var ocupacionesList: List<Role>
    private lateinit var user:User

    private lateinit var teamRepo: TeamRepository
    private lateinit var userRepo: UserRepository
    private lateinit var roleRepo: RoleRepository


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

       // var users = database.kkUsersDao.getAllUsers()

      //  var equipos = database.kkequipostDao.getVisibleEquipos()
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
            val msgJugadores = binding.msgJugadores
            val msgEqTecnico = binding.msgEqTecnico

            msgSelecciona.visibility = View.GONE

            val selectedItem = parent.getItemAtPosition(position) as Team
            // Aquí puedes hacer lo que necesites con el elemento seleccionado
            // Por ejemplo, mostrar un Toast con el elemento seleccionado
            Toast.makeText(requireContext(), "Seleccionaste: $selectedItem", Toast.LENGTH_SHORT).show()
            equipoSeleccionado = selectedItem.teamId

            //  val usersByEquipo = database.kkUsersDao.getUsersByEquipo(equipoSeleccionado)

            var usersNameList= mutableListOf<User>()

            fun mostrarLista(){
                if (usersNameList.isNotEmpty()){
                    msgFotosEmpty.visibility = View.GONE
                    msgJugadores.visibility = View.VISIBLE
                    msgEqTecnico.visibility = View.VISIBLE
                    val rvEquipoTecnico = binding.recyclerViewEquipoTecnico
                    rvEquipoTecnico.visibility = View.VISIBLE
                    val datasetJugadores = mutableListOf<CardData>()
                    val datasetEquipoTecnico = mutableListOf<CardData>()

                    usersNameList.forEach { user ->
                        //var ocupacion = database.kkOcupacionesDao.getOcupacionById(user.ocupacionId)

                        var roleName= Role(0, "")

                        lifecycleScope.launch {
                            try {
                                ocupacionesList = roleRepo.getAllRoles()
                                ocupacionesList.forEach { t ->
                                    if (t.roleId==user.roleId){
                                        roleName=t
                                    }
                                }
                            } catch (getE: GetAllError) {
                                // Mostrar mensaje de error sobre problemas generales durante la creación
                            }
                        }

                        if (user.roleId == 1){
                            datasetJugadores.add(
                                CardData(
                                    user.picture,
                                    user.userId,
                                    user.name,
                                    roleName.roleName

                                )
                            )
                        } else{
                            datasetEquipoTecnico.add(
                                CardData(
                                    user.picture,
                                    user.userId,
                                    user.name,
                                    roleName.roleName
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

            lifecycleScope.launch {
                try {
                    usuariosList = userRepo.getAllUsers()
                    Log.d("FLAG", usuariosList.toString())
                    usuariosList.forEach { t ->
                        if (t.teamId==equipoSeleccionado){
                            usersNameList.add(t)
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

        var equiposNameList= mutableListOf<Team>()
        var ocupacionesNameList= mutableListOf<Role>()

        lifecycleScope.launch {
            try {
                equiposList = teamRepo.getAllTeams()
                equiposList.forEach { t ->
                    if (t.picturesConsent==true){
                        equiposNameList.add(t)
                    }
                }
                ocupacionesList = roleRepo.getAllRoles()
                ocupacionesList.forEach { t ->
                    ocupacionesNameList.add(t)
                }
            } catch (getE: GetAllError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
        }

        val adapterEquipos = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, equiposNameList)
        equipo.setAdapter(adapterEquipos)

        val adapterOcupacion = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ocupacionesNameList)
        ocupacion.setAdapter(adapterOcupacion)

        val dialog = builder.create()
        dialog.show()

        equipo.setOnItemClickListener { parent, view, position, id ->
            var equipoSelected = parent.getItemAtPosition(position) as Team
            equipoSelectedId = equipoSelected.teamId

        }
        ocupacion.setOnItemClickListener { parent, view, position, id ->
            var ocupacionSelected = parent.getItemAtPosition(position) as Role
            ocupacionSelectedId = ocupacionSelected.roleId
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
               // val user = database.kkUsersDao.getUsersById(cardData.id)


                lifecycleScope.launch {
                    try {
                        user = userRepo.getUser(cardData.id)
                    } catch (getE: GetAllError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }

                name.setText(user.name)
                apellido.setText(user.surname)
                img.setImageResource(resources.getIdentifier(user.picture, "drawable", requireContext().packageName))
                fecha.setText(user.dateOfBirth.toString())
                email.setText(user.email)
                if (user.isAdmin==true){
                    isAdmin.isChecked= true
                }
                if (user.isActive==true){
                    isActiva.isChecked = true
                }

                btnBorrar.setOnClickListener {
                    borrarUser(cardData.id)
                    dialog.dismiss()

                }
                btnConfirmar.setOnClickListener {
                    val todoOk = comprobarCampos()
                    if(todoOk){
                        //database.kkUsersDao.update(kkUsersEntity(id = user.id, foto = null, nombre = name.text.toString(), apellido = apellido.text.toString(), mail = email.text.toString(), password = "xxx", fecha_nacimiento = fecha.text.toString(), equipoId = equipoSelectedId, ocupacionId = ocupacionSelectedId, admin = isAdmin.isChecked, activo = isActiva.isChecked ))
                        lifecycleScope.launch {
                            try {
                                val fechaLong = convertirFechaStringAFechaLong(fecha.text.toString())
                                userRepo.update(User(userId = user.userId, token = user.token, picture = null, name = name.text.toString(), surname = apellido.text.toString(), email = email.text.toString(), password = user.password, dateOfBirth = fechaLong, teamId = equipoSelectedId, roleId = ocupacionSelectedId, isAdmin = isAdmin.isChecked, isActive = isActiva.isChecked, isFirstLogin = user.isFirstLogin, lastSeen = user.lastSeen, familyId = user.familyId))
                            } catch (getE: GetAllError) {
                                // Mostrar mensaje de error sobre problemas generales durante la creación
                            }
                        }
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
                        //database.kkUsersDao.insert(kkUsersEntity(foto = null, nombre = name.text.toString(), apellido = apellido.text.toString(), mail = email.text.toString(), password = "xxx", fecha_nacimiento = fecha.text.toString(), equipoId = equipoSelectedId, ocupacionId = ocupacionSelectedId, admin = isAdmin.isChecked, activo = isActiva.isChecked ))
                        val fechaLong = convertirFechaStringAFechaLong(fecha.text.toString())
                        lifecycleScope.launch {
                            try {
                                userRepo.register(User(picture = null, name = name.text.toString(), surname = apellido.text.toString(), email = email.text.toString(), dateOfBirth = fechaLong, teamId = equipoSelectedId, roleId = ocupacionSelectedId, isAdmin = isAdmin.isChecked, isActive = isActiva.isChecked, isFirstLogin = true))
                            } catch (getE: GetAllError) {
                                // Mostrar mensaje de error sobre problemas generales durante la creación
                            }
                        }
                        dialog.dismiss()

                    }
                }
            }
        }



    }


    private fun borrarUser(id:String){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val tv_name = dialogView.findViewById<TextView>(R.id.tv_name)
        //var user = database.kkUsersDao.getUsersById(id)
        lifecycleScope.launch {
            try {
                user = userRepo.getUser(id)
            } catch (getE: GetAllError) {
                // Mostrar mensaje de error sobre problemas generales durante la creación
            }
        }


        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Eliminar el usuario?")
            .setView(dialogView)
            .setPositiveButton("Aceptar") {dialog, _ ->
                //database.kkUsersDao.delete(user)
                lifecycleScope.launch {
                    try {
                        userRepo.delete(user)
                    } catch (getE: GetAllError) {
                        // Mostrar mensaje de error sobre problemas generales durante la creación
                    }
                }

            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
        dialog.show()

        tv_name.text = user.name +" "+ user.surname

    }

    private fun convertirFechaStringAFechaLong(fechaString: String): Long {
        val formato = SimpleDateFormat("dd/MM/yyyy")
        val fecha = formato.parse(fechaString)
        return fecha?.time ?: 0
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
