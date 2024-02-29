package com.txurdinaga.proyectofinaldam.ui

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.FragmentPrincipalBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class PrincipalFragment : Fragment() {
    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!
    private lateinit var layoutPartidosJornada: LinearLayout
    private lateinit var database: kkAppDatabase
    private lateinit var categorias: List<kkCategoryEntity>
    private lateinit var ligas: List<kkLigasEntity>
    lateinit var equiposUnkina: List<kkEquiposEntity>
    lateinit var equipos: List<kkEquiposEntity>
    private var semanaDelAño: Int = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrincipalBinding.inflate(layoutInflater, container, false)
        database = Room.databaseBuilder(
            requireContext(), kkAppDatabase::class.java, kkAppDatabase.DATABASE_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        /*eliminarCategorias()
                eliminarLigas()*/

        categorias = database.kkcategoryDao.getAllCategorias()
        ligas = database.kkligasDao.getAllLigas()

        equiposUnkina = database.kkequipostDao.getAllEquipos().filter { it.isUnkina }
        equipos = database.kkequipostDao.getAllEquipos().filter { !it.isUnkina }


        layoutPartidosJornada = binding.layoutPartidos

        mostrarJornada()


        return binding.root
    }

    @SuppressLint("MissingInflatedId", "ResourceAsColor", "SetTextI18n")
    private fun mostrarJornada() {
        layoutPartidosJornada.removeAllViews()
        var partidosJornada = cogerPartidosJornadaSemana()

        // Inflar tu diseño (layout_equipo.xml) 6 veces y agregarlo al linearGeneral
        for (i in equiposUnkina.indices) {
            var equipo: kkEquiposEntity = equiposUnkina[i]
            var partido: kkPartidosEntity? =
                partidosJornada.find { it.id_equipo1 == equipo.id && equiposUnkina.any { it.id == equipo.id && it.categoria == equipo.categoria } }
            val inflater = layoutInflater
            val layoutPartido = inflater.inflate(R.layout.partidos, null)
            //TODO
            var imgEquipo1 = layoutPartido.findViewById<ImageView>(R.id.imgEquipo1)
            var imgEquipo2 = layoutPartido.findViewById<ImageView>(R.id.imgEquipo2)
            var txtEquipo1 = layoutPartido.findViewById<TextView>(R.id.txtEquipo1)
            var txtEquipo2 = layoutPartido.findViewById<TextView>(R.id.txtEquipo2)
            val txtFechaHora = layoutPartido.findViewById<TextView>(R.id.txtFechaHora)
            val txtCampo = layoutPartido.findViewById<TextView>(R.id.txtCampo)
            val txtResultado = layoutPartido.findViewById<TextView>(R.id.txtResultado)
            if (partido != null) {

                /*val txtCategoria = layoutPartido.findViewById<TextView>(R.id.txtCategoria)
                txtCategoria.text = categorias.find { it.id == equipo.categoria }.toString()*/
                txtFechaHora.text =
                    SimpleDateFormat("dd/MM/yyyy").format(Date(partido.fecha)) + "   " + SimpleDateFormat(
                        "HH:mm"
                    ).format(Date(partido.hora))
                txtCampo.text = partido.local
                if(partido.local == equiposUnkina.find { it.id == partido.id_equipo1 }?.campo){
                    //Unkina
                    imgEquipo1.setImageResource(R.drawable.unkina_sbt_logo)
                    txtEquipo1.text = equipo.name
                    txtEquipo1.setTypeface(null, Typeface.BOLD_ITALIC)
                    //Copntrario
                    imgEquipo2.setImageResource(R.drawable.descansa)
                    txtEquipo2.text = equipos.find { it.id == partido.id_equipo2}?.name
                    txtEquipo2.setTypeface(null, Typeface.NORMAL)
                    txtResultado.text = if(partido.puntos1 != null && partido.puntos2 != null) "${partido.puntos1}  -  ${partido.puntos2}" else "  -  "

                }else{
                    //Unkina
                    imgEquipo2.setImageResource(R.drawable.unkina_sbt_logo)
                    txtEquipo2.text = equipo.name
                    txtEquipo2.setTypeface(null, Typeface.BOLD_ITALIC)
                    //Copntrario
                    imgEquipo1.setImageResource(R.drawable.descansa)
                    txtEquipo1.text = equipos.find { it.id == partido.id_equipo2}?.name
                    txtEquipo1.setTypeface(null, Typeface.NORMAL)
                    txtResultado.text = if(partido.puntos1 != null && partido.puntos2 != null) "${partido.puntos2}  -  ${partido.puntos1}" else "  -  "

                }



            } else {
                /*val txtCategoria = layoutPartido.findViewById<TextView>(R.id.txtCategoria)
                txtCategoria.text = categorias.find { it.id == equipo.categoria }.toString()*/
                txtEquipo1.text = equipo.name
                txtFechaHora.text = obtenerFechaHoraActual()
                txtCampo.setTextColor(R.color.red)
                txtResultado.setTextColor(R.color.red)
                txtFechaHora.setTextColor(R.color.red)
                txtEquipo1.setTextColor(R.color.red)
                txtEquipo2.setTextColor(R.color.red)
                txtEquipo1.setTypeface(null, Typeface.BOLD_ITALIC)

            }

// Agregar el layout al linearGeneral
            layoutPartidosJornada.addView(layoutPartido)

        }

    }

    private fun obtenerFechaHoraActual(): String {
        val formatoFechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val fechaHora = Date()
        return formatoFechaHora.format(fechaHora)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contacto.setOnClickListener {
            findNavController().navigate(R.id.action_principalFragment_to_contactoFragment)
        }


        binding.prueba.setOnClickListener {
            findNavController().navigate(R.id.action_principalFragment_to_pruebaFragment)
        }


    }


}

