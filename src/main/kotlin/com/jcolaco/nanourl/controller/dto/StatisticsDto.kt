package com.jcolaco.nanourl.controller.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class StatisticsDto(
    val url: String,
    val pastDayTotal: Long,
    val pastWeekTotal: Long,
    val allTimeTotal: Long
)
