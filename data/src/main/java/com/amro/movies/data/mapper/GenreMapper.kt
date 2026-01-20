package com.amro.movies.data.mapper

import com.amro.movies.data.remote.resource.GenreResource
import com.amro.movies.domain.model.Genre

fun GenreResource.toDomain(): Genre = Genre(
    id = id,
    name = name
)

fun List<GenreResource>.toDomainList(): List<Genre> = map { it.toDomain() }
