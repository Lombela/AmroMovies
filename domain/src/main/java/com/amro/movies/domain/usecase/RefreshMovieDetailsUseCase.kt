package com.amro.movies.domain.usecase

import com.amro.movies.core.util.Result
import com.amro.movies.domain.repository.MovieRepository
import javax.inject.Inject

class RefreshMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Unit> {
        return movieRepository.refreshMovieDetails(movieId)
    }
}
