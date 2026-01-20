package com.amro.movies.data.remote.resource

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class GenreResource(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class GenreListResource(
    @SerialName("genres")
    val genres: List<GenreResource>
)
