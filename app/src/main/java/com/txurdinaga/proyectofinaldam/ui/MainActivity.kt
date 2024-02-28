package com.txurdinaga.proyectofinaldam.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.data.model.UserBuilder
import com.txurdinaga.proyectofinaldam.data.repo.UserRepository
import com.txurdinaga.proyectofinaldam.databinding.ActivityMainBinding
import com.txurdinaga.proyectofinaldam.ui.util.NetworkConnectivityMonitor
import kotlinx.coroutines.launch
import timber.log.Timber

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
            title="Unkina SBT"
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setCustomView(R.layout.custom_action_bar)
            setDisplayShowCustomEnabled(true)
        }




        val networkMonitor = NetworkConnectivityMonitor(this)
        networkMonitor.observeNetworkConnectivity(this
        ) { isConnected ->
            if (!isConnected) {
                Toast.makeText(this, "Conexión a internet perdida", Toast.LENGTH_SHORT).show()
            }
        }




                // Get a list of style packs that are currently available.
        // Encuentra la imagen por su ID
        var unkinaLogoImageView = findViewById<ImageView>(R.id.unkina_logo)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        unkinaLogoImageView.setOnClickListener {
            navController.navigate(R.id.principalFragment)
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
                R.id.informacionFragment2,
                R.id.gestionLigasFragment,
                R.id.gestionCategoriasFragment,
                R.id.gestionColaboradoresFragment,
                R.id.fotosFragment,
                R.id.loginFragment2,
                R.id.galeriaFragment,
                R.id.gestionPDFs,
                R.id.gestionOcupacionesFragment,
                R.id.personalFragment

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
                destination.id == R.id.colaboradoresFragment2 ||
                destination.id == R.id.gestionLigasFragment ||
                destination.id == R.id.gestionCategoriasFragment ||
                destination.id == R.id.gestionColaboradoresFragment ||
                destination.id == R.id.fotosFragment ||
                destination.id == R.id.loginFragment2 ||
                destination.id == R.id.galeriaFragment ||
                destination.id == R.id.gestionPDFs ||
                destination.id == R.id.gestionOcupacionesFragment ||
                destination.id == R.id.personalFragment
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