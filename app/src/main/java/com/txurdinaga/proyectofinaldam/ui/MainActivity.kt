package com.txurdinaga.proyectofinaldam.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.databinding.ActivityMainBinding
import com.txurdinaga.proyectofinaldam.ui.util.NetworkConnectivityMonitor
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Verificar si es la primera vez que se abre la aplicación
        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("first_time", true)
        if (isFirstTime) {
            setLocale("eu") // Establecer el idioma en euskera
            sharedPreferences.edit().putBoolean("first_time", false).apply() // Marcar que ya no es la primera vez
        } else {
            // Restaurar el idioma seleccionado por el usuario la última vez
            val lastLanguage = sharedPreferences.getString("language", "eu") ?: "eu"
            setLocale(lastLanguage)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        initNavigation()

        val networkMonitor = NetworkConnectivityMonitor(this)
        networkMonitor.observeNetworkConnectivity(this) { isConnected ->
            if (!isConnected) {
                Toast.makeText(this, "Conexión a internet perdida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        // Guardar el último idioma seleccionado
        sharedPreferences.edit().putString("language", language).apply()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHost.navController
        // Especifica los ID de los destinos de nivel superior, excluyendo el ID del destino de inicio
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.principalFragment,
                R.id.gestionEquiposFragment,
                R.id.inscripcionFragment,
                R.id.gestionPartidosFragment,
                R.id.colaboradoresFragment2,
                R.id.informacionFragment2,
                R.id.gestionLigasFragment,
                R.id.gestionCategoriasFragment,
                R.id.gestionColaboradoresFragment,
                R.id.fotosFragment,
                R.id.loginFragment2,
                R.id.galeriaFragment,
                R.id.gestionPDFs,
                R.id.gestionOcupacionesFragment,
                R.id.personalFragment,
                R.id.ajustesFragment2
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == R.id.principalFragment ||
                destination.id == R.id.gestionEquiposFragment ||
                destination.id == R.id.inscripcionFragment ||
                destination.id == R.id.gestionPartidosFragment ||
                destination.id == R.id.informacionFragment2 ||
                destination.id == R.id.colaboradoresFragment2 ||
                destination.id == R.id.gestionLigasFragment ||
                destination.id == R.id.gestionCategoriasFragment ||
                destination.id == R.id.gestionColaboradoresFragment ||
                destination.id == R.id.fotosFragment ||
                destination.id == R.id.loginFragment2 ||
                destination.id == R.id.galeriaFragment ||
                destination.id == R.id.gestionPDFs ||
                destination.id == R.id.gestionOcupacionesFragment ||
                destination.id == R.id.personalFragment ||
                destination.id == R.id.ajustesFragment2
            ) {
                supportActionBar?.title = "Unkina SBT"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
