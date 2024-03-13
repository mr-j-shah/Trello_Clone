package com.crestinfosystems_jinay.trello.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.crestinfosystems_jinay.trello.databinding.HomePageCorouselBinding

class CorouselView(var imageList: List<Int>) :
    RecyclerView.Adapter<CorouselView.ViewHolder>() {


    inner class ViewHolder(val binding: HomePageCorouselBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomePageCorouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.listItemImage.setImageResource(imageList[position])
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
