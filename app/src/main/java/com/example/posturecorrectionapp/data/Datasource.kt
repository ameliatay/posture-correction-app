package com.example.posturecorrectionapp.data

import com.example.posturecorrectionapp.R
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
}