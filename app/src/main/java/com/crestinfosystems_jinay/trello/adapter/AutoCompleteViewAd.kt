package com.crestinfosystems_jinay.trello.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AutoCompleteViewAd(
    context: Context,
    private val items: ArrayList<String>,
    private val suggestionClickListener: (String) -> Unit,

    ) :
    ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1)?.setOnClickListener {
            suggestionClickListener.invoke(getItem(position) ?: "")
        }
        return view
    }

    fun submitList(newData: ArrayList<String>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }
}
