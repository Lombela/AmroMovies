package com.amro.movies.domain.usecase

import com.amro.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIsFavoriteMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(userId: String, movieId: Int): Flow<Boolean> {
        return movieRepository.observeIsFavoriteMovie(userId, movieId)
    }
}
