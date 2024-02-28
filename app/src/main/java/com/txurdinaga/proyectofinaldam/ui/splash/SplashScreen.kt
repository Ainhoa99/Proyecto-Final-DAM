package com.txurdinaga.proyectofinaldam.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.ui.MainActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val imageView: ImageView = findViewById(R.id.logo)

        // Configura la animación
        val scaleAnimation = ScaleAnimation(
            0f, 1f, // Desde la escala X
            0f, 1f, // Desde la escala Y
            Animation.RELATIVE_TO_SELF, 0.5f, // Hacia el centro en X
            Animation.RELATIVE_TO_SELF, 0.5f  // Hacia el centro en Y
        )
        scaleAnimation.duration = 2000 // Duración de la animación en milisegundos

        // Escucha de la animación para hacer la imagen visible al inicio de la animación
        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                imageView.visibility = ImageView.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Puedes realizar acciones adicionales al finalizar la animación si es necesario
                redirectToMainActivity()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // No es necesario implementar en este ejemplo
            }
        })

        // Aplica la animación a la ImageView
        imageView.startAnimation(scaleAnimation)

    }

    private fun redirectToMainActivity() {
        // Starts the main activity after the animation has ended
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}