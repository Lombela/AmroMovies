package com.amro.movies.data.repository

import com.amro.movies.core.util.Result
import com.amro.movies.data.local.LocalMovieDataSource
import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieListEntryEntity
import com.amro.movies.data.local.entity.MovieListMetaEntity
import com.amro.movies.data.local.mapper.toDomain
import com.amro.movies.data.local.mapper.toEntity
import com.amro.movies.data.local.mapper.toGenreCrossRefs
import com.amro.movies.data.local.model.MovieListEntryWithMovie
import com.amro.movies.data.local.mapper.toDomain as genreToDomain
import com.amro.movies.data.local.mapper.toEntity as genreToEntity
import com.amro.movies.data.mapper.toDomain as detailsToDomain
import com.amro.movies.data.remote.api.TmdbApi
import com.amro.movies.data.remote.resource.MovieResource
import com.amro.movies.data.remote.resource.PopularMoviesResource
import com.amro.movies.data.remote.resource.TrendingMoviesResource
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieDetails
import com.amro.movies.domain.model.MovieListType
import com.amro.movies.domain.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbApi: TmdbApi,
    private val localDataSource: LocalMovieDataSource
) : MovieRepository {

    private var cachedGenres: List<GenreEntity>? = null

    override fun observeMovieList(
        userId: String,
        listType: MovieListType,
        limit: Int
    ): Flow<List<Movie>> {
        return localDataSource.observeMovieList(userId, listType.name, limit)
            .mapToDomain()
    }

    override suspend fun refreshMovieList(
        userId: String,
        listType: MovieListType
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val genres = ensureGenresCached()
            val pages = fetchInitialPages(listType)
            val items = pages
                .sortedBy { it.page }
                .flatMap { page ->
                    page.results.map { resource -> MoviePageItem(page.page, resource) }
                }
                .distinctBy { it.resource.id }
                .take(INITIAL_FETCH_LIMIT)

            val now = System.currentTimeMillis()
            val movies = items.map { it.resource.toEntity(now) }
            val crossRefs = items.flatMap { it.resource.toGenreCrossRefs() }
            val listEntries = items.mapIndexed { index, item ->
                MovieListEntryEntity(
                    userId = userId,
                    listType = listType.name,
                    movieId = item.resource.id,
                    position = index,
                    page = item.page,
                    fetchedAtEpochMs = now
                )
            }
            val totalPages = pages.maxOfOrNull { it.totalPages } ?: 0
            val totalResults = pages.firstOrNull()?.totalResults
            val nextPage = if (totalPages > INITIAL_PAGE_COUNT) INITIAL_PAGE_COUNT + 1 else null
            val listMeta = MovieListMetaEntity(
                userId = userId,
                listType = listType.name,
                nextPage = nextPage,
                lastFetchedEpochMs = now,
                totalResults = totalResults
            )

            localDataSource.replaceMovieList(
                userId = userId,
                listType = listType.name,
                genres = genres,
                movies = movies,
                crossRefs = crossRefs,
                listEntries = listEntries,
                listMeta = listMeta,
                maxCacheSize = MAX_CACHED_MOVIES
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun loadMoreMovieList(
        userId: String,
        listType: MovieListType
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val nextPage = localDataSource.getNextPage(userId, listType.name)
                ?: return@withContext Result.Success(Unit)
            val genres = ensureGenresCached()
            val response = fetchPage(listType, nextPage)
            val existingIds = localDataSource.getListMovieIds(userId, listType.name).toSet()
            val newItems = response.results.filter { it.id !in existingIds }
            val now = System.currentTimeMillis()
            val startPosition = (localDataSource.getMaxPosition(userId, listType.name) ?: -1) + 1
            val listEntries = newItems.mapIndexed { index, resource ->
                MovieListEntryEntity(
                    userId = userId,
                    listType = listType.name,
                    movieId = resource.id,
                    position = startPosition + index,
                    page = response.page,
                    fetchedAtEpochMs = now
                )
            }
            val movies = newItems.map { it.toEntity(now) }
            val crossRefs = newItems.flatMap { it.toGenreCrossRefs() }
            val newNextPage = if (response.page < response.totalPages) response.page + 1 else null
            val listMeta = MovieListMetaEntity(
                userId = userId,
                listType = listType.name,
                nextPage = newNextPage,
                lastFetchedEpochMs = now,
                totalResults = response.totalResults
            )

            localDataSource.appendMovieList(
                genres = genres,
                movies = movies,
                crossRefs = crossRefs,
                listEntries = listEntries,
                listMeta = listMeta,
                maxCacheSize = MAX_CACHED_MOVIES
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGenres(): Result<List<Genre>> = withContext(Dispatchers.IO) {
        try {
            val genres = ensureGenresCached().map { it.genreToDomain() }
            Result.Success(genres)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> = withContext(Dispatchers.IO) {
        try {
            val response = tmdbApi.getMovieDetails(movieId)
            Result.Success(response.detailsToDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun ensureGenresCached(): List<GenreEntity> {
        cachedGenres?.let { return it }
        val localGenres = localDataSource.getGenres()
        if (localGenres.isNotEmpty()) {
            cachedGenres = localGenres
            return localGenres
        }

        val response = tmdbApi.getGenres()
        val genres = response.genres.map { it.genreToEntity() }
        localDataSource.upsertGenres(genres)
        cachedGenres = genres
        return genres
    }

    private suspend fun fetchInitialPages(listType: MovieListType): List<MoviePageResult> {
        return coroutineScope {
            (1..INITIAL_PAGE_COUNT)
                .map { page -> async { fetchPage(listType, page) } }
                .awaitAll()
        }
    }

    private suspend fun fetchPage(
        listType: MovieListType,
        page: Int
    ): MoviePageResult {
        return when (listType) {
            MovieListType.TRENDING -> tmdbApi.getTrendingMovies(page).toPageResult()
            MovieListType.POPULAR -> tmdbApi.getPopularMovies(page).toPageResult()
        }
    }

    private fun TrendingMoviesResource.toPageResult() =
        MoviePageResult(
            page = page,
            results = results,
            totalPages = totalPages,
            totalResults = totalResults
        )

    private fun PopularMoviesResource.toPageResult() =
        MoviePageResult(
            page = page,
            results = results,
            totalPages = totalPages,
            totalResults = totalResults
        )

    private data class MoviePageResult(
        val page: Int,
        val results: List<MovieResource>,
        val totalPages: Int,
        val totalResults: Int
    )

    private data class MoviePageItem(
        val page: Int,
        val resource: MovieResource
    )

    private fun Flow<List<MovieListEntryWithMovie>>.mapToDomain() =
        map { entries -> entries.map { it.movie.toDomain() } }

    private companion object {
        const val INITIAL_PAGE_COUNT = 5
        const val INITIAL_FETCH_LIMIT = 100
        const val MAX_CACHED_MOVIES = 200
    }
}
