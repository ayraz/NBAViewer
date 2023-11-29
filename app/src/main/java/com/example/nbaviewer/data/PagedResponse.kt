package com.example.nbaviewer.data

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    val data: List<T>,
    val meta: Meta
)

@Serializable
data class Meta(
    val total_pages: Int,
    val current_page: Int,
    val next_page: Int,
    val per_page: Int,
    val total_count: Int
)
