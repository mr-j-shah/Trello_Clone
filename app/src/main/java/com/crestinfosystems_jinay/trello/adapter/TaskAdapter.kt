package com.crestinfosystems_jinay.trello.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crestinfosystems_jinay.trello.Screens_Activity.TaskDetails.TaskDetails
import com.crestinfosystems_jinay.trello.data.Board
import com.crestinfosystems_jinay.trello.data.State
import com.crestinfosystems_jinay.trello.data.Task
import com.crestinfosystems_jinay.trello.databinding.TaskCardTileBinding

class TaskAdapter(
    private var items: ArrayList<Task>,
    private var context: Context,
    private var board: Board,
) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: TaskCardTileBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            TaskCardTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            var intent = Intent(context, TaskDetails::class.java)
            intent.putExtra("task", items[position])
            intent.putExtra("board", board)
            ContextCompat.startActivity(context, intent, null)
        }
        with(holder) {
            with(items[position]) {
                when (items[position].state) {
                    State.todo -> binding.projectCard.setCardBackgroundColor(Color.parseColor("#FFEB3B"))
                    State.doing -> binding.projectCard.setCardBackgroundColor(Color.parseColor("#4CAF50"))
                    State.done -> binding.projectCard.setCardBackgroundColor(Color.parseColor("#B0BEC5"))
                }
                binding.boardTitle.text = items[position].title
                binding.boardDesc.text = items[position].desc
                binding.state.text = items[position].state.toString().uppercase()
                binding.lastEditBy.text = items[position].lastEdit
            }
        }
    }

    fun submitList(newData: ArrayList<Task>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }
}