package com.crestinfosystems_jinay.trello.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.crestinfosystems_jinay.trello.Screens_Activity.setting.projects.Detail_Project_View
import com.crestinfosystems_jinay.trello.data.Board
import com.crestinfosystems_jinay.trello.databinding.BoardCardTileBinding

class BoardRecycleViewAd(
    private var items: List<Board>,
    var context: Context,
    private var onClick: () -> Unit,

    ) :
    RecyclerView.Adapter<BoardRecycleViewAd.ViewHolder>() {

    inner class ViewHolder(var binding: BoardCardTileBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            BoardCardTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            var intent = Intent(context, Detail_Project_View::class.java)
            intent.putExtra("board", items[position])
            startActivity(context, intent, null)
        }
        with(holder) {
            with(items[position]) {
                binding.boardTitle.text = items[position].name
                binding.boardDesc.text = items[position].des
                binding.tileNextScreen.setOnClickListener {
                    var intent = Intent(context, Detail_Project_View::class.java)
                    startActivity(context, intent, null)
                }
            }
        }
    }

    fun submitList(newData: List<Board>) {
        items = newData
        notifyDataSetChanged()
    }
}