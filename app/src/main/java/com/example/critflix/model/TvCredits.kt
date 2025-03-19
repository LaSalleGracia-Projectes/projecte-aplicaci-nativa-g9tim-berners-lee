package com.example.critflix.model

data class TvCredits(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)