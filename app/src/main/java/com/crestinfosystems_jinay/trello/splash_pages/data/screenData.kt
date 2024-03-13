package com.crestinfosystems_jinay.trello.splash_pages.data

import com.crestinfosystems_jinay.trello.R

data class screenData(var index: Int, var drawableImage: Int, var description: String)

val listOfPages: List<screenData> = listOf(
    screenData(
        0,
        R.drawable.ic_splash_page1,
        "Plan your tasks to do, that way you’ll stay organized and you won’t skip any"
    ),
    screenData(
        1,
        R.drawable.ic_splash_page2,
        "Make a full schedule for the whole week and stay organized and productive all days"
    ),
    screenData(
        2,
        R.drawable.ic_splash_page3,
        "create a team task, invite people and manage your work together"
    ),
    screenData(3, R.drawable.ic_splash_page4, "You informations are secure with us")
)
