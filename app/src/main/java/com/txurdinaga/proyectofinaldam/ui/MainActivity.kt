package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        initNavigation()


        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setCustomView(R.layout.custom_action_bar)
            setDisplayShowCustomEnabled(true)
        }
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHost.navController
        // Aquí especificamos los ID de los destinos de nivel superior, excluyendo el ID del destino de inicio
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.principalFragment,
                R.id.gestionEquiposFragment,
                R.id.inscripcionFragment,
                R.id.gestionPartidosFragment,
                R.id.colaboradoresFragment2,
                R.id.informacionFragment2
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Escuchar cambios en los destinos del NavController
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            if (destination.id == R.id.principalFragment ||
                destination.id == R.id.gestionEquiposFragment ||
                destination.id == R.id.inscripcionFragment ||
                destination.id == R.id.gestionPartidosFragment ||
                destination.id == R.id.informacionFragment2 ||
                destination.id == R.id.colaboradoresFragment2
            ) {
                supportActionBar?.title = "Unkina SBT" // Establecer el título deseado para los destinos específicos
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}