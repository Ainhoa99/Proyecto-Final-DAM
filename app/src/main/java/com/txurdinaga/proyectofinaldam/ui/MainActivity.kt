package com.txurdinaga.proyectofinaldam.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
                showNoInternetDialog(this)
            }
        }

        // Inicialización del las funciones de utilidad de encrypted shared prefs como singleton
        // Lo inicializamos aquí pasándole el ApplicationContext global para no tener que hacerlo
        // en los repositorios
        EncryptedPrefsUtil.init(this.applicationContext)

        supportActionBar?.apply {
            title="Unkina SBT"
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setCustomView(R.layout.custom_action_bar)
            setDisplayShowCustomEnabled(true)
        }

        // Encuentra la imagen por su ID
        var unkinaLogoImageView = findViewById<ImageView>(R.id.unkina_logo)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        unkinaLogoImageView.setOnClickListener {
            navController.navigate(R.id.principalFragment)
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
        mostrarOpciones()
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
                R.id.gestionFotosFragment,
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
                destination.id == R.id.ajustesFragment2 ||
                destination.id == R.id.cerrarSesionFragment ||
                destination.id == R.id.gestionFotosFragment
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
    fun showNoInternetDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(false)

        val btnRetry = view.findViewById<Button>(R.id.btn_retry)
        btnRetry.setOnClickListener {
            if (isInternetAvailable(context)) {
                dialog.dismiss()
            } else {
                // Retrasa la verificación de la conexión a Internet durante unos segundos antes de intentarlo nuevamente
                CoroutineScope(Dispatchers.Main).launch {
                    showNoInternetDialog(context) // Verifica la conexión a Internet nuevamente
                }
            }
        }

        dialog.show()
    }
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    private fun mostrarOpciones() {
        val navView = binding.navView
        val menu = navView.menu
        var usuario :Boolean? =null
        try {
            if(EncryptedPrefsUtil.getString("tokenLogin")!=null){

                usuario = EncryptedPrefsUtil.getBoolean("isAdmin")
                todoATrue()
                val gPartidos = menu.findItem(R.id.gestionPartidosFragment)
                val gEquipos = menu.findItem(R.id.gestionEquiposFragment)
                val gCategorias = menu.findItem(R.id.gestionCategoriasFragment)
                val gPatrocinadores = menu.findItem(R.id.gestionColaboradoresFragment)
                val gLigas = menu.findItem(R.id.gestionLigasFragment)
                val gOcupaciones = menu.findItem(R.id.gestionOcupacionesFragment)
                val gFotos = menu.findItem(R.id.gestionFotosFragment)
                val gPDFs = menu.findItem(R.id.gestionPDFs)
                val inicioSesion = menu.findItem(R.id.loginFragment2)
                val login = menu.findItem(R.id.loginFragment2)

                gPartidos.isVisible = usuario
                gEquipos.isVisible = usuario
                gCategorias.isVisible = usuario
                gPatrocinadores.isVisible = usuario
                gLigas.isVisible = usuario
                gOcupaciones.isVisible = usuario
                gFotos.isVisible = usuario
                gPDFs.isVisible = usuario
                inicioSesion.isVisible = usuario
                login.isVisible = false

            }else {
                usuarioInvitado()
            }
        }catch (e: Exception){
            usuarioInvitado()
        }

    }

    private fun usuarioInvitado() {
        val navView = binding.navView
        val menu = navView.menu
        todoATrue()
        val principal = menu.findItem(R.id.principalFragment)
        val gestionEquipos = menu.findItem(R.id.gestionEquiposFragment)
        val inscripcion = menu.findItem(R.id.inscripcionFragment)
        val gestionPartidos = menu.findItem(R.id.gestionPartidosFragment)
        val colaboradores = menu.findItem(R.id.colaboradoresFragment2)
        val informacion = menu.findItem(R.id.informacionFragment2)
        val gestionLigas = menu.findItem(R.id.gestionLigasFragment)
        val gestionCategorias = menu.findItem(R.id.gestionCategoriasFragment)
        val gestionColaboradores = menu.findItem(R.id.gestionColaboradoresFragment)
        val gestionFotos = menu.findItem(R.id.gestionFotosFragment)
        val fotos = menu.findItem(R.id.fotosFragment)
        val login = menu.findItem(R.id.loginFragment2)
        val galeria = menu.findItem(R.id.galeriaFragment)
        val gestionPDFs = menu.findItem(R.id.gestionPDFs)
        val gestionOcupaciones = menu.findItem(R.id.gestionOcupacionesFragment)
        val personal = menu.findItem(R.id.personalFragment)
        val ajustes = menu.findItem(R.id.ajustesFragment2)
        val cerrarSesion = menu.findItem(R.id.cerrarSesionFragment)

        gestionEquipos.isVisible = false
        gestionPartidos.isVisible = false
        gestionLigas.isVisible = false
        gestionCategorias.isVisible = false
        gestionColaboradores.isVisible = false
        gestionFotos.isVisible = false
        fotos.isVisible = false
        galeria.isVisible = false
        gestionPDFs.isVisible = false
        gestionOcupaciones.isVisible = false
        personal.isVisible = false
        cerrarSesion.isVisible = false
    }

    fun todoATrue(){
        val navView = binding.navView
        val menu = navView.menu
        val principal = menu.findItem(R.id.principalFragment)
        val gestionEquipos = menu.findItem(R.id.gestionEquiposFragment)
        val inscripcion = menu.findItem(R.id.inscripcionFragment)
        val gestionPartidos = menu.findItem(R.id.gestionPartidosFragment)
        val gestionFotos = menu.findItem(R.id.gestionFotosFragment)
        val colaboradores = menu.findItem(R.id.colaboradoresFragment2)
        val informacion = menu.findItem(R.id.informacionFragment2)
        val gestionLigas = menu.findItem(R.id.gestionLigasFragment)
        val gestionCategorias = menu.findItem(R.id.gestionCategoriasFragment)
        val gestionColaboradores = menu.findItem(R.id.gestionColaboradoresFragment)
        val fotos = menu.findItem(R.id.fotosFragment)
        val login = menu.findItem(R.id.loginFragment2)
        val galeria = menu.findItem(R.id.galeriaFragment)
        val gestionPDFs = menu.findItem(R.id.gestionPDFs)
        val gestionOcupaciones = menu.findItem(R.id.gestionOcupacionesFragment)
        val personal = menu.findItem(R.id.personalFragment)
        val ajustes = menu.findItem(R.id.ajustesFragment2)
        val cerrarSesion = menu.findItem(R.id.cerrarSesionFragment)

        principal.isVisible = true
        gestionEquipos.isVisible = true
        inscripcion.isVisible = true
        gestionPartidos.isVisible = true
        gestionFotos.isVisible = true
        colaboradores.isVisible = true
        //informacion.isVisible = true
        gestionLigas.isVisible = true
        gestionCategorias.isVisible = true
        gestionColaboradores.isVisible = true
        fotos.isVisible = true
        login.isVisible = true
        galeria.isVisible = true
        gestionPDFs.isVisible = true
        gestionOcupaciones.isVisible = true
        personal.isVisible = true
        ajustes.isVisible = true
        cerrarSesion.isVisible = true
    }


}
