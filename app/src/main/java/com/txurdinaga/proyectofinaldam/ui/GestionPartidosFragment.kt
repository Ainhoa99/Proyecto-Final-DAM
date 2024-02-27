package com.txurdinaga.proyectofinaldam.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentGestionPartidosBinding
import com.txurdinaga.proyectofinaldam.ui.splash.PdfGenerator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class GestionPartidosFragment : Fragment() {
    private var _binding: FragmentGestionPartidosBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageViewDate: ImageView
    private var semanaDelAño: Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    private lateinit var layoutPartidosJornada: LinearLayout
    private lateinit var database: kkAppDatabase
    private lateinit var listaPartidos: List<kkPartidosEntity>
    private lateinit var categorias: List<kkCategoryEntity>
    private lateinit var ligas: List<kkLigasEntity>
    lateinit var equiposUnkina: List<kkEquiposEntity>
    lateinit var equipos: List<kkEquiposEntity>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGestionPartidosBinding.inflate(layoutInflater, container, false)
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        /*eliminarCategorias()
                eliminarLigas()*/
        añadirCategorias()
        añadirLigas()

        categorias = database.kkcategoryDao.getAllCategorias()
        ligas = database.kkligasDao.getAllLigas()

        //eliminarEquipos()
        añadirEquipos()
        equiposUnkina = database.kkequipostDao.getAllEquipos().filter { it.isUnkina }
        equipos = database.kkequipostDao.getAllEquipos().filter { !it.isUnkina }

        imageViewDate = binding.imageViewDate

        imageViewDate.setOnClickListener {
            showDatePickerDialogJornada()
        }

        layoutPartidosJornada = binding.layoutPartidos

        mostrarJornada()

        val btnGenerarFichero = binding.btnGenerarFichero
        btnGenerarFichero.setOnClickListener {
            val pdfGenerator = PdfGenerator(requireContext())
            pdfGenerator.generateAndDownloadPdf(
                "Jornada $semanaDelAño",
                cogerPartidosJornadaSemana(),
                equiposUnkina,
                equipos,
                categorias
            )

        }

        return binding.root
    }

    private fun mostrarJornada() {
        listaPartidos = database.kkpartidosDao.getAllPartidos()
        layoutPartidosJornada.removeAllViews()
        var partidosJornada = cogerPartidosJornadaSemana()

        // Inflar tu diseño (layout_equipo.xml) 6 veces y agregarlo al linearGeneral
        for (i in 0 until 6) {
            var equipo: kkEquiposEntity = equiposUnkina[i]
            var partido: kkPartidosEntity? =
                partidosJornada.find { it.id_equipo1 == equipo.id && equiposUnkina.any { it.id == equipo.id && it.categoria == equipo.categoria } }
            val inflater = layoutInflater
            val layoutPartido = inflater.inflate(R.layout.card_partido, null)
            layoutPartido.tag = "0"
            if (partido != null) {
                val txtCategoria = layoutPartido.findViewById<TextView>(R.id.txtCategoria)
                txtCategoria.text = categorias.find { it.id == equipo.categoria }.toString()
                val txtEquipo1 = layoutPartido.findViewById<TextView>(R.id.txtEquipo1)
                txtEquipo1.text = equipo.name
                val txtFecha = layoutPartido.findViewById<TextView>(R.id.txtFecha)
                txtFecha.text = SimpleDateFormat("dd/MM/yyyy").format(Date(partido.fecha))
                val txtHora = layoutPartido.findViewById<TextView>(R.id.txtHora)
                txtHora.text = SimpleDateFormat("HH:mm").format(Date(partido.hora))
                val txtEquipo2 = layoutPartido.findViewById<TextView>(R.id.txtEquipo2)
                txtEquipo2.text = equipos.find { it.id == partido.id_equipo2 }.toString()
                val txtCampo = layoutPartido.findViewById<TextView>(R.id.txtCampo)
                txtCampo.text = partido.local
                val txtResultado = layoutPartido.findViewById<TextView>(R.id.txtResultado)
                if (partido.puntos1 == null || partido.puntos2 == null) {
                    txtResultado.text = " - "
                } else {
                    txtResultado.text = "${partido.puntos1}   -   ${partido.puntos2}"
                }
                layoutPartido.tag = partido.id.toString()
            } else {
                val txtCategoria = layoutPartido.findViewById<TextView>(R.id.txtCategoria)
                txtCategoria.text = categorias.find { it.id == equipo.categoria }.toString()
                val txtEquipo1 = layoutPartido.findViewById<TextView>(R.id.txtEquipo1)
                txtEquipo1.text = equipo.name
            }

            layoutPartido.setOnClickListener {
                mostrarPopUp(equipo, layoutPartido)
            }


// Agregar el layout al linearGeneral
            layoutPartidosJornada.addView(layoutPartido)

        }

    }

    private fun mostrarPopUp(equipo: kkEquiposEntity, layout: View) {
        val tag = layout.tag.toString().toInt()
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.card_editar_partido)

            // Establecer el tamaño del pop-up al 90% del tamaño de la pantalla
            window?.attributes?.apply {
                width = (resources.displayMetrics.widthPixels * 0.95).toInt()
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }

            val txtFecha = findViewById<TextView>(R.id.txtFecha).apply {
                setOnClickListener { showDatePickerDialog(this) }
            }

            val txtHora = findViewById<TextView>(R.id.txtHora).apply {
                setOnClickListener { showTimePickerDialog(this) }
            }

            val spnEquipoContrario = findViewById<Spinner>(R.id.spnEquipoContrario)

            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                equipos.filter { it.liga == equipo.liga }
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnEquipoContrario.adapter = adapter

            spnEquipoContrario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Handle item selection here
                    // Do something with the selectedEquipo if needed
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected
                }
            }

            findViewById<TextView>(R.id.txtCategoria).text =
                categorias.find { it.id == equipo.categoria }.toString()
            findViewById<TextView>(R.id.txtEquipo1).text = equipo.name

            val txtPuntos1 = findViewById<EditText>(R.id.etnPuntosFavor)
            val txtPuntos2 = findViewById<EditText>(R.id.etnPuntosContra)

            if (tag != 0) {
                val partido = listaPartidos.find { it.id == tag.toLong() }
                partido?.let {
                    txtFecha.text = SimpleDateFormat("dd/MM/yyyy").format(Date(it.fecha))
                    txtHora.text = SimpleDateFormat("HH:mm").format(Date(it.hora))
                    findViewById<CheckBox>(R.id.chkLocal).isChecked = it.local.equals("Usansolo")
                    val equipo2 = equipos.find { equipo2 -> equipo2.id == it.id_equipo2 }
                    val position =
                        (spnEquipoContrario.adapter as ArrayAdapter<kkEquiposEntity>).getPosition(equipo2)
                    spnEquipoContrario.setSelection(position)
                    if (it.puntos1 != null) {
                        txtPuntos1.text = it.puntos1.toString().toEditable()
                        txtPuntos2.text = it.puntos2.toString().toEditable()
                    }
                }
            }

            // Evitar que el pop-up se cierre al tocar fuera de él
            setCanceledOnTouchOutside(false)

            findViewById<ImageView>(R.id.btnClose).setOnClickListener { cancel() }
            show()

            findViewById<Button>(R.id.btnConfirmar).setOnClickListener {
                val local: String? = if (findViewById<CheckBox>(R.id.chkLocal).isChecked) {
                    equipo.campo
                } else {
                    (spnEquipoContrario.selectedItem as? kkEquiposEntity)?.campo ?: ""
                }

                val equipoContrario = spnEquipoContrario.selectedItem as? kkEquiposEntity
                if (equipoContrario != null) {
                    if (txtFecha.text.isEmpty()) {
                        txtFecha.setBackgroundResource(R.drawable.borde_error)
                    } else if (txtHora.text.isEmpty()) {
                        txtHora.setBackgroundResource(R.drawable.borde_error)
                    } else {
                        val fecha = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).parse(txtFecha.text.toString())?.time ?: 0L
                        val hora =
                            SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).parse(txtHora.text.toString())?.time
                                ?: 0L
                        if ((txtPuntos1.text.isEmpty() || txtPuntos2.text.isEmpty()) && (txtPuntos1.text.isNotEmpty() || txtPuntos2.text.isNotEmpty())) {
                            if (txtPuntos1.text.isEmpty()) {
                                txtPuntos1.setBackgroundResource(R.drawable.borde_error)
                            } else {
                                txtPuntos2.setBackgroundResource(R.drawable.borde_error)
                            }
                        } else {
                            val puntos1: Int? = txtPuntos1.text.toString().toIntOrNull()
                            val puntos2: Int? = txtPuntos2.text.toString().toIntOrNull()

                            if (tag == 0) {
                                val partido = kkPartidosEntity(
                                    id_equipo1 = equipo.id,
                                    id_equipo2 = equipoContrario.id,
                                    puntos1 = puntos1,
                                    puntos2 = puntos2,
                                    fecha = fecha,
                                    hora = hora,
                                    local = local!!
                                )
                                database.kkpartidosDao.insertarPartido(partido)
                            } else {
                                val partido = kkPartidosEntity(
                                    id = tag.toLong(),
                                    id_equipo1 = equipo.id,
                                    id_equipo2 = equipoContrario.id,
                                    puntos1 = puntos1,
                                    puntos2 = puntos2,
                                    fecha = fecha,
                                    hora = hora,
                                    local = local!!
                                )
                                database.kkpartidosDao.insertarPartido(partido)
                            }

                            mostrarJornada()
                            cancel()
                        }
                    }
                }
            }
        }
    }
    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


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
                txtInsertarFecha.setBackgroundResource(R.drawable.borde)
            },
            year,
            month,
            day
        )

// Configurar el primer día de la semana en domingo
        datePickerDialog.datePicker.firstDayOfWeek = Calendar.SUNDAY

// Restaurar el idioma por defecto después de la creación del DatePickerDialog
        Locale.setDefault(Locale.getDefault())

// Configurar el primer día de la semana en domingo
        datePickerDialog.datePicker.firstDayOfWeek = Calendar.SUNDAY

// Establecer la fecha mínima permitida para la selección (domingo de la semana anterior)
        val minDateCalendar = Calendar.getInstance()
        minDateCalendar.set(Calendar.WEEK_OF_YEAR, semanaDelAño - 1) // Semana anterior
        minDateCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY) // Establecer el domingo de esa semana
        datePickerDialog.datePicker.minDate = minDateCalendar.timeInMillis

// Establecer la fecha máxima permitida para la selección (sábado de la misma semana)
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.set(Calendar.WEEK_OF_YEAR, semanaDelAño) // Establecer la misma semana del año
        maxDateCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY) // Establecer el sábado de esa semana
        datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis

        datePickerDialog.show()
    }

    private fun showDatePickerDialogJornada() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthOfYear, dayOfMonth ->
                val fechaSeleccionada = "$dayOfMonth/${monthOfYear + 1}/$yearSelected"
                val date =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaSeleccionada)
                semanaDelAño = obtenerNumeroSemana(date)
                mostrarJornada()
            }, year, month, day
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(txtInsertarHora: TextView) {
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Selecciona la hora del partido")
                .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                .build()

        picker.addOnPositiveButtonClickListener {
            val selectedHour = picker.hour
            val selectedMinute = picker.minute

            val formattedTime =
                String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)

            txtInsertarHora.text = formattedTime
            txtInsertarHora.setBackgroundResource(R.drawable.borde)
        }

        picker.show(requireActivity().supportFragmentManager, "tag") // Reemplaza "tag" con una etiqueta adecuada
    }

    private fun cogerPartidosJornadaSemana(): List<kkPartidosEntity> {
        var lista: MutableList<kkPartidosEntity> = mutableListOf()
        for (partido in database.kkpartidosDao.getAllPartidos()) {
            if (obtenerNumeroSemana(Date(partido.fecha)) == semanaDelAño) {
                lista.add(partido)
            }
        }
        return lista
    }

    private fun obtenerNumeroSemana(fecha: Date): Int {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.time = fecha

        // Ajustar el primer día de la semana a domingo
        calendar.firstDayOfWeek = Calendar.SUNDAY

        // Obtener el día de la semana (domingo = 1, lunes = 2, ..., sábado = 7)
        val diaSemana = calendar.get(Calendar.DAY_OF_WEEK)

        // Ajustar la fecha al domingo de la semana actual si es domingo, de lo contrario, ir al domingo anterior
        if (diaSemana == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, 0) // No es necesario ajustar la fecha si es domingo
        } else {
            calendar.add(Calendar.DAY_OF_WEEK, -(diaSemana - 1))
        }

        // Obtener el número de semana
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }
    /*private fun eliminarLigas() {
        database.kkligasDao.deleteLigas()
    }*/

    private fun añadirLigas() {
        database.kkligasDao.insert(kkLigasEntity(id = 1, name = "Senior Masculina"))
        database.kkligasDao.insert(kkLigasEntity(id = 2, name = "Junior Cadete Mixto Escolar"))
        database.kkligasDao.insert(kkLigasEntity(id = 3, name = "Infantil Fem.B"))
        database.kkligasDao.insert(kkLigasEntity(id = 4, name = "Minibasket Femenino D"))
        database.kkligasDao.insert(kkLigasEntity(id = 5, name = "Premini 3x3 5x5 D"))
    }

    /*private fun eliminarCategorias() {
        database.kkcategoryDao.deleteCategorias()

    }*/

    private fun añadirCategorias() {
        database.kkcategoryDao.insert(kkCategoryEntity(id = 1, name = "Senior Masculina"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 2, name = "Junior Cadete Mixto Escolar"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 3, name = "Infantil Femenino B"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 4, name = "Minibasket Femenino D"))
        database.kkcategoryDao.insert(kkCategoryEntity(id = 5, name = "Premini 3x3 5x5 D"))
    }

    /*private fun eliminarEquipos() {
        database.kkequipostDao.deleteEquipo()
    }*/

    private fun añadirEquipos() {
        // Buscar los ids de categoría y liga para Senior Masculino
        val seniorMasculinoCategoriaId = categorias.find { it.id.toInt() == 1 }?.id ?: 0
        val seniorMasculinoLigaId = ligas.find { it.id.toInt() == 1 }?.id ?: 0

        // Buscar los ids de categoría y liga para Junior Masculino
        val juniorMasculinoCategoriaId = categorias.find { it.id.toInt() == 2 }?.id ?: 0
        val juniorMasculinoLigaId = ligas.find { it.id.toInt() == 2 }?.id ?: 0

        // Buscar los ids de categoría y liga para Senior Masculino
        val infantilFemeninoCategoriaId = categorias.find { it.id.toInt() == 3 }?.id ?: 0
        val infantilFemeninoLigaId = ligas.find { it.id.toInt() == 3 }?.id ?: 0

        // Buscar los ids de categoría y liga para Junior Masculino
        val minibasketFemeninoCategoriaId = categorias.find { it.id.toInt() == 4 }?.id ?: 0
        val minibasketFemeninoLigaId = ligas.find { it.id.toInt() == 4 }?.id ?: 0

        // Buscar los ids de categoría y liga para Senior Masculino
        val preminiCategoriaId = categorias.find { it.id.toInt() == 5 }?.id ?: 0
        val preminiLigaId = ligas.find { it.id.toInt() == 5 }?.id ?: 0


        // Crear instancias de Equipo y insertar en la base de datos
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 1,
                name = "UNKINAKO 2",
                campo = "Usansolo",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 2,
                name = "ALKIHAIZEA UNKINA",
                campo = "Usansolo",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 3,
                name = "KIRAM UNKINA",
                campo = "Usansolo",
                categoria = infantilFemeninoCategoriaId,
                liga = infantilFemeninoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 4,
                name = "KIRAM UNKINA",
                campo = "Usansolo",
                categoria = minibasketFemeninoCategoriaId,
                liga = minibasketFemeninoLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 5,
                name = "UNKINA 14",
                campo = "Usansolo",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 6,
                name = "UNKINA 15",
                campo = "Usansolo",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = true,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 7,
                name = "Erandio Altzaga",
                campo = "Erandio",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 8,
                name = "Erandio Altzaga",
                campo = "Erandio",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 9,
                name = "Gaztelueta",
                campo = "Leioa",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 10,
                name = "Gaztelueta",
                campo = "Leioa",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 11,
                name = "Berangoko Hontzuriak",
                campo = "Berango",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 12,
                name = "Berangoko Hontzuriak",
                campo = "Berango",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 13,
                name = "Udalaitz",
                campo = "Elorrio",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 14,
                name = "Udalaitz",
                campo = "Elorrio",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 15,
                name = "Trapaga Saskibaloia",
                campo = "Trapaga",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 16,
                name = "Trapaga Saskibaloia",
                campo = "Trapaga",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )

        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 17,
                name = "Kibuc Basauri",
                campo = "Basauri",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 18,
                name = "Kibuc Basauri",
                campo = "Basauri",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 19,
                name = "Tabirako San Antonio",
                campo = "Durango",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 20,
                name = "Tabirako Kurutxiaga",
                campo = "Durango",
                categoria = infantilFemeninoCategoriaId,
                liga = infantilFemeninoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 21,
                name = "Urdaneta",
                campo = "Loiu",
                categoria = minibasketFemeninoCategoriaId,
                liga = minibasketFemeninoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 22,
                name = "Tabirako San Antonio",
                campo = "Durango",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 23,
                name = "Tabirako San Antonio",
                campo = "Loiu",
                categoria = preminiCategoriaId,
                liga = preminiLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 23,
                name = "Uribike Ugeraga",
                campo = "Sopelana",
                categoria = seniorMasculinoCategoriaId,
                liga = seniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
        database.kkequipostDao.insert(
            kkEquiposEntity(
                id = 24,
                name = "Berriz SBT",
                campo = "Berriz",
                categoria = juniorMasculinoCategoriaId,
                liga = juniorMasculinoLigaId,
                escudo = "",
                isUnkina = false,
                visible = true
            )
        )
    }
}