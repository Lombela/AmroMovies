package com.amro.movies.domain.usecase

import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.repository.MovieRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return movieRepository.getGenres()
    }
}
