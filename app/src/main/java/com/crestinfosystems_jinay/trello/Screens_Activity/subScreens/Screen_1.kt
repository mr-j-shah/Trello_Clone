package com.crestinfosystems_jinay.trello.Screens_Activity.subScreens

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.crestinfosystems_jinay.trello.R
import com.crestinfosystems_jinay.trello.adapter.CorouselView
import com.crestinfosystems_jinay.trello.data.State
import com.crestinfosystems_jinay.trello.data.Task
import com.crestinfosystems_jinay.trello.databinding.FragmentScreen1Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Screen_1 : Fragment() {

    var lastUpdateTask: Task? = null
    var binding: FragmentScreen1Binding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreen1Binding.inflate(inflater, container, false)
        getLastEditTask()
        setCourouselAdapter()
        return binding?.root
    }

    fun jsonStringToMap(jsonString: String): Map<String, Any> {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return Gson().fromJson(jsonString, type) ?: emptyMap()
    }

    private fun getLastEditTask() {
        val applicationContext: Context = requireContext()
        val preferences =
            applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        if (preferences.contains("lastedit")) {
            val lastedit = preferences.getString("lastedit", "")
            val resultMap: Map<String, Any> = jsonStringToMap(lastedit!!)
            lastUpdateTask = Task(resultMap)
            binding?.lastEditBy?.boardTitle?.text = lastUpdateTask?.title
            binding?.lastEditBy?.boardDesc?.text = lastUpdateTask?.desc
            binding?.lastEditBy?.lastEditBy?.visibility = View.GONE
            binding?.lastEditBy?.state?.text = lastUpdateTask?.state.toString().uppercase()
            when (lastUpdateTask?.state) {
                State.todo -> binding?.lastEditBy?.projectCard?.setCardBackgroundColor(
                    Color.parseColor(
                        "#FFEB3B"
                    )
                )

                State.doing -> binding?.lastEditBy?.projectCard?.setCardBackgroundColor(
                    Color.parseColor(
                        "#4CAF50"
                    )
                )

                State.done -> binding?.lastEditBy?.projectCard?.setCardBackgroundColor(
                    Color.parseColor(
                        "#B0BEC5"
                    )
                )

                else -> {}
            }
        } else {
            binding?.lastEditBy?.projectCard?.visibility = View.GONE
        }
    }

    private fun setCourouselAdapter() {
        val arrayList = ArrayList<Int>()
        arrayList.add(R.drawable.slider_1)
        arrayList.add(R.drawable.slider_2)
        arrayList.add(R.drawable.slider_3)
        arrayList.add(R.drawable.slider_4)
        val adapter = CorouselView(arrayList)
        binding?.recycler?.setAdapter(adapter)
    }
}