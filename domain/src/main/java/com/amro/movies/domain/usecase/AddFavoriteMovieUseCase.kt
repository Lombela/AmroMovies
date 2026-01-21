package com.amro.movies.domain.usecase

import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.MovieDetails
import com.amro.movies.domain.repository.MovieRepository
import javax.inject.Inject

class AddFavoriteMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(userId: String, movieDetails: MovieDetails): Result<Unit> {
        return movieRepository.addFavoriteMovie(userId, movieDetails)
    }
}
