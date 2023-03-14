package com.example.posturecorrectionapp.data

import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.CategoryCard
import com.example.posturecorrectionapp.models.Exercises
import com.example.posturecorrectionapp.models.ProgramCard

class Datasource {

    fun loadJumpBackIn() : List<ProgramCard> {
        return listOf<ProgramCard>(
            ProgramCard(R.string.category1, R.string.program1, R.string.rating4_7, R.string.duration1h30m, R.mipmap.walking),
            ProgramCard(R.string.category1, R.string.program2, R.string.rating5_0, R.string.duration30m, R.mipmap.barre4),
            ProgramCard(R.string.category1, R.string.program3, R.string.rating4_6, R.string.duration50m, R.mipmap.crossfit2)
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
}