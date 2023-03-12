package com.example.posturecorrectionapp.data

import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.CategoryCard
import com.example.posturecorrectionapp.models.Exercises
import com.example.posturecorrectionapp.models.ProgramCard

class Datasource {

    fun loadJumpBackIn() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category1, R.string.program1, R.string.rating4_7, R.string.duration1h30m, R.drawable.walking),
            ProgramCard(R.string.category1, R.string.program2, R.string.rating5_0, R.string.duration30m, R.drawable.barre4),
            ProgramCard(R.string.category1, R.string.program3, R.string.rating4_6, R.string.duration50m, R.drawable.crossfit2)
        )
    }

    fun loadRecommended() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category2, R.string.program5, R.string.rating4_7, R.string.duration1h30m, R.drawable.barre1),
            ProgramCard(R.string.category2, R.string.program6, R.string.rating5_0, R.string.duration30m, R.drawable.crossfit4),
            ProgramCard(R.string.category2, R.string.program3, R.string.rating4_6, R.string.duration50m, R.drawable.crossfit1),
            ProgramCard(R.string.category2, R.string.program4, R.string.rating4_6, R.string.duration50m, R.drawable.yoga2)
        )
    }

    fun loadRecentlyCompleted() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category3, R.string.program4, R.string.rating4_7, R.string.duration1h30m, R.drawable.yoga3),
            ProgramCard(R.string.category3, R.string.program5, R.string.rating5_0, R.string.duration30m, R.drawable.yoga5),
            ProgramCard(R.string.category3, R.string.program2, R.string.rating4_6, R.string.duration50m, R.drawable.barre5)
        )
    }

    fun loadPracticeCategories() : List<CategoryCard> {
        return listOf<CategoryCard>(
            CategoryCard(R.string.category1, R.mipmap.jogging),
            CategoryCard(R.string.category2, R.drawable.barre5),
            CategoryCard(R.string.category3, R.drawable.crossfit2),
            CategoryCard(R.string.category4, R.drawable.walking),
            CategoryCard(R.string.category5, R.drawable.yoga3),
            CategoryCard(R.string.category6, R.drawable.yoga5),
        )
    }

    fun loadExercises() : List<Exercises> {
        return listOf<Exercises>(
            Exercises(R.string.exercise1, R.string.category1, R.string.beginner, R.drawable.crossfit2),
            Exercises(R.string.exercise2, R.string.category1, R.string.advanced, R.drawable.crossfit3),
            Exercises(R.string.exercise3, R.string.category1, R.string.beginner, R.drawable.crossfit4),
            Exercises(R.string.exercise4, R.string.category1, R.string.intermediate, R.drawable.barre5),
            Exercises(R.string.exercise1, R.string.category1, R.string.beginner, R.drawable.crossfit2),
            Exercises(R.string.exercise2, R.string.category1, R.string.advanced, R.drawable.crossfit3),
            Exercises(R.string.exercise3, R.string.category1, R.string.beginner, R.drawable.crossfit4),
            Exercises(R.string.exercise4, R.string.category1, R.string.intermediate, R.drawable.barre5),
        )
    }
}