package rma.catquiz.cats.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResultDto(
    val nickname: String,
    val result: Float,
    val category: Int
)