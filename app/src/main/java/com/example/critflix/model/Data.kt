package com.example.critflix.model

data class Data(
    val page: Int,
    val results: List<PelisPopulares>,
    val total_pages: Int,
    val total_results: Int
)