package com.txurdinaga.proyectofinaldam.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.txurdinaga.proyectofinaldam.databinding.FragmentAjustesBinding
import java.util.*

class AjustesFragment : Fragment() {

    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE)

        // Restaurar la preferencia de idioma guardada
        val currentLanguage = sharedPreferences.getString("language", Locale.getDefault().language) // Por defecto, usa el idioma del dispositivo

        // Establecer el estado del interruptor de idioma según el idioma actual
        binding.languageSwitch.isChecked = currentLanguage == "es"

        // Manejar el cambio de idioma cuando se activa el interruptor
        binding.languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newLanguage = if (isChecked) "es" else "eu"
            sharedPreferences.edit().putString("language", newLanguage).apply()
            updateAppLanguage(newLanguage)
        }

        return binding.root
    }

    // Función para cambiar el idioma en toda la aplicación
    private fun updateAppLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = requireActivity().baseContext.resources.configuration
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        // Re-crear la vista para aplicar los cambios de idioma
        requireActivity().recreate()
    }

    override fun onResume() {
        super.onResume()

        // Restaurar la preferencia de idioma guardada al volver a la vista de ajustes
        val currentLanguage = sharedPreferences.getString("language", Locale.getDefault().language)
        binding.languageSwitch.isChecked = currentLanguage == "es"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


