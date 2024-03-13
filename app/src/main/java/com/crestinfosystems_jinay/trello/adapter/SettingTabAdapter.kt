package com.crestinfosystems_jinay.trello.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crestinfosystems_jinay.trello.data.SettinsTilesFilelds
import com.crestinfosystems_jinay.trello.databinding.SettingFieldsTilesBinding

class  SettingTabAdapter(var list: List<SettinsTilesFilelds>) :
    RecyclerView.Adapter<SettingTabAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: SettingFieldsTilesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            SettingFieldsTilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.tileIcon.setImageResource(list[position].icon)
                binding.tileTitle.text = list[position].title
                binding.tileNextScreen.setOnClickListener {
                    list[position].onclick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
