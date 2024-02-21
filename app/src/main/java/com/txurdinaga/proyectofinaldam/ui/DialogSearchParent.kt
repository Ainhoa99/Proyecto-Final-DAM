import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.txurdinaga.proyectofinaldam.R

class DialogSearchParent(context: Context, private val names: Array<String>) : Dialog(context) {
    private lateinit var searchEditText: EditText
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Llama a requestWindowFeature() antes de setContentView()
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_searchparent)

        searchEditText = findViewById(R.id.search_edit_text)
        listView = findViewById(R.id.listView)
        val acceptButton = findViewById<Button>(R.id.dialog_button)


        acceptButton.setOnClickListener {
            // Aquí puedes definir el comportamiento del botón de aceptar
            dismiss() // Cerrar el diálogo después de hacer algo
        }

        // Crear un adaptador para la lista y establecerlo en el ListView
        adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter

        // Manejar la búsqueda
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar este método
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar este método
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim()
                filterList(searchText)
            }
        })
    }

    private fun filterList(searchText: String) {
        val filteredNames = names.filter { it.contains(searchText, ignoreCase = true) }
        adapter.clear()
        adapter.addAll(filteredNames)
        adapter.notifyDataSetChanged()
    }
}
