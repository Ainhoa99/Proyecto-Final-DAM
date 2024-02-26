package com.txurdinaga.proyectofinaldam.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class Pdf(private val context: Context) {
    private var downloadID: Long = 0

    fun descargarPDF(url: String) {
        val fileName = "nombre_del_archivo.pdf"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("Nombre del PDF")
        request.setDescription("Descargando PDF")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        // Especifica la ruta de la carpeta de descargas del dispositivo
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manager.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val pdfFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                    )
                    openPDF(pdfFile)
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun openPDF(pdfFile: File) {
        if (pdfFile.exists()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "El archivo PDF no se encontrÃ³", Toast.LENGTH_SHORT).show()
        }
    }
}
