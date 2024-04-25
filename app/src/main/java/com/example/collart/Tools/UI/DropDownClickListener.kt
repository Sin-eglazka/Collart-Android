package com.example.collart.Tools.UI

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView

class DropDownClickListener(
    private val context: Context,
    private val professions: List<String>,
    private val onItemClick: (String) -> Unit // Callback function to handle item selection
) : View.OnClickListener {
    override fun onClick(v: View) {
        val dialog = Dialog(context)
        dialog.setContentView(com.example.collart.R.layout.dialog_searchable_spinner)
        dialog.window?.setLayout(650, 800)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val editText: EditText = dialog.findViewById(com.example.collart.R.id.edit_text)
        val listView: ListView = dialog.findViewById(com.example.collart.R.id.list_view)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            android.R.layout.simple_list_item_1,
            professions
        )

        listView.adapter = adapter
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = adapter.getItem(position)
                if (selectedItem != null) {
                    onItemClick(selectedItem)
                } // Call the callback function with the selected item

                dialog.dismiss()
            }
    }
}
