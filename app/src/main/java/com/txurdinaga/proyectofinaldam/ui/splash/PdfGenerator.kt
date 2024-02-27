/*package com.txurdinaga.gestionpartidos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import com.itextpdf.layout.element.Cell
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPRow
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

class PdfGenerator(private val context: Context) {

    fun generateAndDownloadPdf(
        content: String,
        fileName: String,
        context: Context
    ) {
        val document = Document()

        // Directorio de almacenamiento externo
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "PDFs"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Archivo PDF
        val file = File(directory, "$fileName.pdf")

        val fileOutputStream = FileOutputStream(file)
        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

        document.open()

        // Agregar contenido al PDF
        document.add(Paragraph(content))

        // Crear una tabla con 4 columnas
        for (fila in 1..3) {
            val table = PdfPTable(7)
            table.spacingAfter = 5f // Ajusta el valor según tus necesidades

            val cellTitulo = PdfPCell(Phrase("Titulo", Font(Font.FontFamily.HELVETICA, 20f, Font.BOLDITALIC, BaseColor(ContextCompat.getColor(context, R.color.blue)))))
            cellTitulo.colspan = 7
            cellTitulo.horizontalAlignment = Element.ALIGN_CENTER
            cellTitulo.setPadding(5f)
            cellTitulo.minimumHeight = 20f
            table.addCell(cellTitulo)

            val txtFecha = PdfPCell(Phrase("Fecha"))
            txtFecha.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtFecha)

            val txtHora = PdfPCell(Phrase("Hora"))
            txtHora.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtHora)

            val txtLugar = PdfPCell(Phrase("Lugar"))
            txtLugar.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtLugar)

            val txtLugarMostrar = PdfPCell(Phrase("Usansolo"))
            txtLugarMostrar.horizontalAlignment = Element.ALIGN_CENTER
            txtLugarMostrar.colspan = 4
            table.addCell(txtLugarMostrar)
            val txtFechaMostrar = PdfPCell(Phrase("xx/xx/xxxx", Font(Font.FontFamily.TIMES_ROMAN, 11.5f, Font.NORMAL, BaseColor(ContextCompat.getColor(context, R.color.black)))))
            txtFechaMostrar.horizontalAlignment = Element.ALIGN_CENTER
            val colorInt = ContextCompat.getColor(context, R.color.red)
            txtFechaMostrar.backgroundColor = BaseColor(colorInt)
            txtFechaMostrar.setPadding(2.5f)
            txtFechaMostrar.minimumHeight = 20f

            table.addCell(txtFechaMostrar)

            val txtHoraMostrar = PdfPCell(Phrase("xx:xx"))
            txtHoraMostrar.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtHoraMostrar)

            val txtEquipos = PdfPCell(Phrase("Equipos"))
            txtEquipos.horizontalAlignment = Element.ALIGN_CENTER
            txtEquipos.colspan = 5
            table.addCell(txtEquipos)

            document.add(table)
        }

        /*// Fila 1: 1 celda
        val cell1 = PdfPCell(Phrase("Dato 1, Fila 1"))
        cell1.colspan = 2 // Esta celda abarcará 4 columnas
        cell1.rowspan = 2 // Esta celda abarcará 4 columnas
        table.addCell(cell1)

        // Fila 2: 4 columnas
        table.addCell(PdfPCell(Phrase("Dato 1, Fila 2")))
        table.addCell(PdfPCell(Phrase("Dato 2, Fila 2")))
        table.addCell(PdfPCell(Phrase("Dato 3, Fila 2")))
        table.addCell(PdfPCell(Phrase("Dato 4, Fila 2")))

        // Fila 3: 3 columnas
        table.addCell(PdfPCell(Phrase("Dato 1, Fila 3")))
        table.addCell(PdfPCell(Phrase("Dato 2, Fila 3")))
        table.addCell(PdfPCell(Phrase("Dato 3, Fila 3")))
*/
        // Agregar la tabla al documento

        document.close()
        fileOutputStream.close()

        // Lanzar la intención de descarga
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        context.startActivity(intent)
    }
}*/
package com.txurdinaga.proyectofinaldam.ui.splash

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.txurdinaga.proyectofinaldam.R
import com.txurdinaga.proyectofinaldam.ui.kkCategoryEntity
import com.txurdinaga.proyectofinaldam.ui.kkEquiposEntity
import com.txurdinaga.proyectofinaldam.ui.kkPartidosEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class PdfGenerator(private val context: Context) {

    fun generateAndDownloadPdf(
        fileName: String,
        partidos: List<kkPartidosEntity>,
        equiposUnkina: List<kkEquiposEntity>,
        equipos: List<kkEquiposEntity>,
        categorias: List<kkCategoryEntity>,

        ) {
        val document = Document()

        // Directorio de almacenamiento externo
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "PDFs"
        )

        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Archivo PDF
        val file = File(directory, "$fileName.pdf")

        val fileOutputStream = FileOutputStream(file)
        val pdfWriter = PdfWriter.getInstance(document, fileOutputStream)

        document.open()
        val listaEquipos: MutableList<kkEquiposEntity> = equiposUnkina.toMutableList()
        // Crear una tabla con 4 columnas

        for (i in listaEquipos.indices){
            if(partidos.find { it.id_equipo1 == listaEquipos[i].id }?.id_equipo1 == listaEquipos[i].id){
                val partido = partidos.find { it.id_equipo1 == listaEquipos[i].id }
                val table = PdfPTable(7)
                table.spacingAfter = 35f // Ajusta el valor según tus necesidades
                val cellTitulo = PdfPCell(
                    Phrase(
                        equiposUnkina.find { it.id == partido?.id_equipo1 }?.name,
                        Font(
                            Font.FontFamily.HELVETICA,
                            20f,
                            Font.BOLDITALIC,
                            BaseColor(ContextCompat.getColor(context, R.color.blue))
                        )
                    )
                )
                cellTitulo.colspan = 7
                cellTitulo.horizontalAlignment = Element.ALIGN_CENTER
                cellTitulo.setPadding(5f)
                cellTitulo.minimumHeight = 20f
                table.addCell(cellTitulo)

                val txtFecha = PdfPCell(Phrase("Fecha"))
                txtFecha.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtFecha)

                val txtHora = PdfPCell(Phrase("Hora"))
                txtHora.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtHora)

                val txtLugar = PdfPCell(Phrase("Lugar"))
                txtLugar.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtLugar)

                val txtLugarMostrar = PdfPCell(Phrase(partido?.local))
                txtLugarMostrar.horizontalAlignment = Element.ALIGN_CENTER
                txtLugarMostrar.colspan = 4
                table.addCell(txtLugarMostrar)
                val txtFechaMostrar = PdfPCell(
                    Phrase(
                        SimpleDateFormat("dd/MM/yyyy").format(Date(partido!!.fecha)),
                        Font(
                            Font.FontFamily.TIMES_ROMAN,
                            11.5f,
                            Font.NORMAL,
                            BaseColor(ContextCompat.getColor(context, R.color.black))
                        )
                    )
                )
                txtFechaMostrar.horizontalAlignment = Element.ALIGN_CENTER
                var colorInt = ContextCompat.getColor(context, R.color.lightBlue)
                txtFechaMostrar.backgroundColor = BaseColor(colorInt)
                txtFechaMostrar.setPadding(2.5f)
                txtFechaMostrar.minimumHeight = 20f

                table.addCell(txtFechaMostrar)

                val txtHoraMostrar = PdfPCell(
                    Phrase(
                        SimpleDateFormat("HH:mm").format(Date(partido.hora)),
                        Font(
                            Font.FontFamily.TIMES_ROMAN,
                            11.5f,
                            Font.NORMAL,
                            BaseColor(ContextCompat.getColor(context, R.color.black))
                        )
                    )
                )
                colorInt = ContextCompat.getColor(context, R.color.lightBlue)
                txtHoraMostrar.backgroundColor = BaseColor(colorInt)
                txtHoraMostrar.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtHoraMostrar)

                val txtEquipos = PdfPCell(
                    Phrase(if(partido.local == listaEquipos.find { it.id == partido.id_equipo1 }?.campo) "${
                        equiposUnkina.find { it.id == partido.id_equipo1 }.toString()
                    } - ${equipos.find { it.id == partido.id_equipo2 }.toString()}" else "${equipos.find { it.id == partido.id_equipo2 }.toString()} - ${
                        equiposUnkina.find { it.id == partido.id_equipo1 }.toString()
                    }",
                        Font(
                            Font.FontFamily.HELVETICA,
                            12.5f,
                            Font.NORMAL,
                            BaseColor(ContextCompat.getColor(context, R.color.black))
                        )
                    )
                )
                txtEquipos.horizontalAlignment = Element.ALIGN_CENTER
                txtEquipos.colspan = 5
                table.addCell(txtEquipos)

                document.add(table)
            }else{
                val equipo = listaEquipos[i]
                val table = PdfPTable(7)
                table.spacingAfter = 35f // Ajusta el valor según tus necesidades
                val cellTitulo = PdfPCell(
                    Phrase(
                        equipo.name,
                        Font(
                            Font.FontFamily.HELVETICA,
                            20f,
                            Font.BOLDITALIC,
                            BaseColor(ContextCompat.getColor(context, R.color.blue))
                        )
                    )
                )
                cellTitulo.colspan = 7
                cellTitulo.horizontalAlignment = Element.ALIGN_CENTER
                cellTitulo.setPadding(5f)
                cellTitulo.minimumHeight = 20f
                table.addCell(cellTitulo)

                val txtFecha = PdfPCell(Phrase("Fecha"))
                txtFecha.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtFecha)

                val txtHora = PdfPCell(Phrase("Hora"))
                txtHora.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtHora)

                val txtLugar = PdfPCell(Phrase("Lugar"))
                txtLugar.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtLugar)

                val txtLugarMostrar = PdfPCell(Phrase(""))
                txtLugarMostrar.horizontalAlignment = Element.ALIGN_CENTER
                txtLugarMostrar.colspan = 4
                table.addCell(txtLugarMostrar)
                val txtFechaMostrar = PdfPCell(
                    Phrase(
                        "",
                        Font(
                            Font.FontFamily.TIMES_ROMAN,
                            11.5f,
                            Font.NORMAL,
                            BaseColor(ContextCompat.getColor(context, R.color.black))
                        )
                    )
                )
                txtFechaMostrar.horizontalAlignment = Element.ALIGN_CENTER
                var colorInt = ContextCompat.getColor(context, R.color.lightBlue)
                txtFechaMostrar.backgroundColor = BaseColor(colorInt)
                txtFechaMostrar.setPadding(2.5f)
                txtFechaMostrar.minimumHeight = 20f

                table.addCell(txtFechaMostrar)

                val txtHoraMostrar = PdfPCell(
                    Phrase(
                        "",
                        Font(
                            Font.FontFamily.TIMES_ROMAN,
                            11.5f,
                            Font.NORMAL,
                            BaseColor(ContextCompat.getColor(context, R.color.black))
                        )
                    )
                )
                colorInt = ContextCompat.getColor(context, R.color.lightBlue)
                txtHoraMostrar.backgroundColor = BaseColor(colorInt)

                txtHoraMostrar.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(txtHoraMostrar)

                val txtEquipos = PdfPCell(
                    Phrase(
                        equipo.name,
                        Font(
                            Font.FontFamily.HELVETICA,
                            12.5f,
                            Font.NORMAL,
                            BaseColor(ContextCompat.getColor(context, R.color.black))
                        )
                    )
                )
                txtEquipos.horizontalAlignment = Element.ALIGN_CENTER
                txtEquipos.colspan = 5
                table.addCell(txtEquipos)

                document.add(table)
            }
        }




        /*// Fila 1: 1 celda
        val cell1 = PdfPCell(Phrase("Dato 1, Fila 1"))
        cell1.colspan = 2 // Esta celda abarcará 4 columnas
        cell1.rowspan = 2 // Esta celda abarcará 4 columnas
        table.addCell(cell1)

        // Fila 2: 4 columnas
        table.addCell(PdfPCell(Phrase("Dato 1, Fila 2")))
        table.addCell(PdfPCell(Phrase("Dato 2, Fila 2")))
        table.addCell(PdfPCell(Phrase("Dato 3, Fila 2")))
        table.addCell(PdfPCell(Phrase("Dato 4, Fila 2")))

        // Fila 3: 3 columnas
        table.addCell(PdfPCell(Phrase("Dato 1, Fila 3")))
        table.addCell(PdfPCell(Phrase("Dato 2, Fila 3")))
        table.addCell(PdfPCell(Phrase("Dato 3, Fila 3")))
*/
        // Agregar la tabla al documento

        document.close()
        fileOutputStream.close()
    }

  /*  private fun mostrarPartidoVacio() {
        for (equipo in listaEquipos) {
            val table = PdfPTable(7)
            table.spacingAfter = 35f // Ajusta el valor según tus necesidades
            val cellTitulo = PdfPCell(
                Phrase(
                    categorias.find { it.id == equipo.categoria }.toString(),
                    Font(
                        Font.FontFamily.HELVETICA,
                        20f,
                        Font.BOLDITALIC,
                        BaseColor(ContextCompat.getColor(context, R.color.blue))
                    )
                )
            )
            cellTitulo.colspan = 7
            cellTitulo.horizontalAlignment = Element.ALIGN_CENTER
            cellTitulo.setPadding(5f)
            cellTitulo.minimumHeight = 20f
            table.addCell(cellTitulo)

            val txtFecha = PdfPCell(Phrase("Fecha"))
            txtFecha.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtFecha)

            val txtHora = PdfPCell(Phrase("Hora"))
            txtHora.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtHora)

            val txtLugar = PdfPCell(Phrase("Lugar"))
            txtLugar.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtLugar)

            val txtLugarMostrar = PdfPCell(Phrase(""))
            txtLugarMostrar.horizontalAlignment = Element.ALIGN_CENTER
            txtLugarMostrar.colspan = 4
            table.addCell(txtLugarMostrar)
            val txtFechaMostrar = PdfPCell(
                Phrase(
                    "",
                    Font(
                        Font.FontFamily.TIMES_ROMAN,
                        11.5f,
                        Font.NORMAL,
                        BaseColor(ContextCompat.getColor(context, R.color.black))
                    )
                )
            )
            txtFechaMostrar.horizontalAlignment = Element.ALIGN_CENTER
            var colorInt = ContextCompat.getColor(context, R.color.lighterRed)
            txtFechaMostrar.backgroundColor = BaseColor(colorInt)
            txtFechaMostrar.setPadding(2.5f)
            txtFechaMostrar.minimumHeight = 20f

            table.addCell(txtFechaMostrar)

            val txtHoraMostrar = PdfPCell(
                Phrase(
                    "",
                    Font(
                        Font.FontFamily.TIMES_ROMAN,
                        11.5f,
                        Font.NORMAL,
                        BaseColor(ContextCompat.getColor(context, R.color.black))
                    )
                )
            )
            colorInt = ContextCompat.getColor(context, R.color.lightestRed)
            txtHoraMostrar.backgroundColor = BaseColor(colorInt)

            txtHoraMostrar.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(txtHoraMostrar)

            val txtEquipos = PdfPCell(
                Phrase(
                    equipo.nombre,
                    Font(
                        Font.FontFamily.HELVETICA,
                        12.5f,
                        Font.NORMAL,
                        BaseColor(ContextCompat.getColor(context, R.color.black))
                    )
                )
            )
            txtEquipos.horizontalAlignment = Element.ALIGN_CENTER
            txtEquipos.colspan = 5
            table.addCell(txtEquipos)

            document.add(table)
        }
    }*/

}

