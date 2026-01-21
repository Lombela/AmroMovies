package com.amro.movies.domain.usecase

import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.MovieListType
import com.amro.movies.domain.repository.MovieRepository
import javax.inject.Inject

class LoadMoreTrendingMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return movieRepository.loadMoreMovieList(userId, MovieListType.TRENDING)
    }
}
