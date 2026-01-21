package com.amro.movies.domain.usecase

import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieListType
import com.amro.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(userId: String): Flow<List<Movie>> {
        return movieRepository.observeMovieList(userId, MovieListType.POPULAR)
    }
}
