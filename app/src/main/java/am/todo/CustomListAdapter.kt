package am.todo

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CustomListAdapter (private val context: Activity, private val title: MutableList<String>, private val description: MutableList<String>)
    : ArrayAdapter<String>(context, R.layout.todo_list_item, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.todo_list_item, null, true)

        val titleText = rowView.findViewById(R.id.todo_name) as TextView
        val subtitleText = rowView.findViewById(R.id.todo_description) as TextView

        titleText.text = title[position]
        subtitleText.text = description[position]

        return rowView
    }
}