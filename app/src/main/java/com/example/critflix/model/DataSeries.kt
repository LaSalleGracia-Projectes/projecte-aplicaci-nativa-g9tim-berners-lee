package com.example.critflix.model

data class DataSeries(
    val page: Int,
    val results: List<SeriesPopulares>,
    val total_pages: Int,
    val total_results: Int
)