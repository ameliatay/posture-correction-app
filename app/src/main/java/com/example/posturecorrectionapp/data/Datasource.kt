package com.example.posturecorrectionapp.data

import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.*

class Datasource {

    fun loadProfile():List<ListItem>{
        return listOf<ListItem>(
            ListItem("Profile", R.drawable.icon_profile),
            ListItem("Notifications", R.drawable.icon_notification),
            ListItem("Security", R.drawable.icon_security),
            ListItem("Help", R.drawable.icon_help),
            ListItem("Dark Theme", R.drawable.icon_profile),
            ListItem("Logout", R.drawable.icon_profile)
        )
    }
    // programs
    fun loadHiitProgram() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category5, R.string.hiit3, R.string.rating4_2, R.string.duration40m, R.mipmap.hiit3),
            ProgramCard(R.string.category8, R.string.hiit1, R.string.rating4_5, R.string.duration50m, R.mipmap.hiit1),
            ProgramCard(R.string.category7, R.string.hiit4, R.string.rating4_8, R.string.duration45m, R.mipmap.hiit4),
            ProgramCard(R.string.category1, R.string.hiit5, R.string.rating4_7, R.string.duration1h, R.mipmap.hiit5),
            ProgramCard(R.string.category6, R.string.hiit2, R.string.rating4_6, R.string.duration15m, R.mipmap.hiit2),
        )
    }

    fun loadBarreProgram() : List<ProgramCard>{
        return listOf<ProgramCard>(
            ProgramCard(R.string.category5, R.string.barre1, R.string.rating4_4, R.string.duration45m, R.mipmap.barre1),
            ProgramCard(R.string.category6, R.string.barre2, R.string.rating4_7, R.string.duration30m, R.mipmap.barre2),
            ProgramCard(R.string.category5, R.string.barre3, R.string.rating4_8, R.string.duration50m, R.mipmap.barre3),
            ProgramCard(R.string.category9, R.string.barre4, R.string.rating4_5, R.string.duration40m, R.mipmap.barre4),
            ProgramCard(R.string.category7, R.string.barre5, R.string.rating4_9, R.string.duration1h, R.mipmap.barre5)
        )
    }

    fun loadCrossfitProgram() : List<ProgramCard>{
        return listOf<ProgramCard>(
            ProgramCard(R.string.category5, R.string.crossfit1, R.string.rating4_5, R.string.duration50m, R.mipmap.crossfit1),
            ProgramCard(R.string.category4, R.string.crossfit2, R.string.rating4_2, R.string.duration40m, R.mipmap.crossfit2),
            ProgramCard(R.string.category7, R.string.crossfit3, R.string.rating4_7, R.string.duration1h, R.mipmap.crossfit3),
            ProgramCard(R.string.category4, R.string.crossfit4, R.string.rating4_6, R.string.duration45m, R.mipmap.crossfit4),
            ProgramCard(R.string.category1, R.string.crossfit5, R.string.rating4_4, R.string.duration30m, R.mipmap.crossfit5)
        )
    }

    fun loadPilatesProgram() : List<ProgramCard>{
        return listOf<ProgramCard>(
            ProgramCard(R.string.category1, R.string.pilates1, R.string.rating4_7, R.string.duration45m, R.mipmap.pilates1),
            ProgramCard(R.string.category5, R.string.pilates2, R.string.rating4_9, R.string.duration1h, R.mipmap.pilates2),
            ProgramCard(R.string.category9, R.string.pilates3, R.string.rating4_5, R.string.duration30m, R.mipmap.pilates3),
            ProgramCard(R.string.category6, R.string.pilates4, R.string.rating4_6, R.string.duration40m, R.mipmap.pilates4),
            ProgramCard(R.string.category7, R.string.pilates5, R.string.rating4_8, R.string.duration50m, R.mipmap.pilates5)
        )
    }

    fun loadCarouselItems() : List<CarouselItem> {
        return listOf<CarouselItem>(
            CarouselItem(R.mipmap.sit_ups_anim, R.string.itemTitle1, R.string.itemSubtitle1, 1),
            CarouselItem(R.mipmap.programs_page, R.string.itemTitle2, R.string.itemSubtitle2, 2),
            CarouselItem(R.mipmap.practice_page, R.string.itemTitle3, R.string.itemSubtitle3, 3),
            CarouselItem(R.mipmap.home_page, R.string.itemTitle4, R.string.itemSubtitle4, 4),
            CarouselItem(R.mipmap.running_anim, R.string.itemTitle5, R.string.itemSubtitle5, 5)
        )
    }

    fun loadJumpBackIn() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category1, R.string.program1, R.string.rating4_7, R.string.duration1h30m, R.mipmap.hiit1),
            ProgramCard(R.string.category1, R.string.program2, R.string.rating5_0, R.string.duration30m, R.mipmap.hiit2),
            ProgramCard(R.string.category1, R.string.program3, R.string.rating4_6, R.string.duration50m, R.mipmap.hiit3)
        )
    }

    fun loadRecommended() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category2, R.string.program5, R.string.rating4_7, R.string.duration1h30m, R.mipmap.barre1),
            ProgramCard(R.string.category2, R.string.program6, R.string.rating5_0, R.string.duration30m, R.mipmap.crossfit4),
            ProgramCard(R.string.category2, R.string.program3, R.string.rating4_6, R.string.duration50m, R.mipmap.crossfit1),
            ProgramCard(R.string.category2, R.string.program4, R.string.rating4_6, R.string.duration50m, R.mipmap.yoga2)
        )
    }

    fun loadRecentlyCompleted() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category3, R.string.program4, R.string.rating4_7, R.string.duration1h30m, R.mipmap.yoga3),
            ProgramCard(R.string.category3, R.string.program5, R.string.rating5_0, R.string.duration30m, R.mipmap.yoga5),
            ProgramCard(R.string.category3, R.string.program2, R.string.rating4_6, R.string.duration50m, R.mipmap.barre5)
        )
    }

    fun loadPracticeCategories() : List<CategoryCard> {
        return listOf<CategoryCard>(
            CategoryCard(R.string.category1, R.mipmap.jogging),
            CategoryCard(R.string.category2, R.mipmap.barre5),
            CategoryCard(R.string.category3, R.mipmap.crossfit2),
            CategoryCard(R.string.category4, R.mipmap.walking),
            CategoryCard(R.string.category5, R.mipmap.yoga3),
            CategoryCard(R.string.category6, R.mipmap.yoga5),
            CategoryCard(R.string.category7, R.mipmap.barre4),
            CategoryCard(R.string.category8, R.mipmap.crossfit1),
            CategoryCard(R.string.category9, R.mipmap.yoga1),
        )
    }

    fun loadExercises() : List<Exercises> {
        return listOf<Exercises>(
            Exercises(R.string.exercise1, R.string.category1, R.string.beginner, R.mipmap.crossfit2),
            Exercises(R.string.exercise2, R.string.category1, R.string.advanced, R.mipmap.crossfit3),
            Exercises(R.string.exercise3, R.string.category1, R.string.beginner, R.mipmap.crossfit4),
            Exercises(R.string.exercise4, R.string.category1, R.string.intermediate, R.mipmap.barre5),
            Exercises(R.string.exercise1, R.string.category1, R.string.beginner, R.mipmap.crossfit2),
            Exercises(R.string.exercise2, R.string.category1, R.string.advanced, R.mipmap.crossfit3),
            Exercises(R.string.exercise3, R.string.category1, R.string.beginner, R.mipmap.crossfit4),
            Exercises(R.string.exercise4, R.string.category1, R.string.intermediate, R.mipmap.barre5),
        )
    }

    fun loadExercisesDemo() : List<Exercises> {
        return listOf<Exercises>(
            Exercises(R.string.exercise5, R.string.category10, R.string.beginner, R.mipmap.crossfit2),
            Exercises(R.string.exercise1, R.string.category10, R.string.beginner, R.mipmap.crossfit3),
        )
    }
}