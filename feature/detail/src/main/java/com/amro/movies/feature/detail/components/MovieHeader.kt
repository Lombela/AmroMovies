package com.amro.movies.feature.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.amro.movies.feature.detail.R
import com.amro.movies.core.util.Constants
import com.amro.movies.domain.model.MovieDetails
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MovieHeader(
    movieDetails: MovieDetails,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = Constants.buildBackdropUrl(movieDetails.backdropPath)
                    ?: Constants.buildPosterUrl(movieDetails.posterPath),
                contentDescription = stringResource(R.string.movie_backdrop),
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Poster
            AsyncImage(
                model = Constants.buildPosterUrl(movieDetails.posterPath),
                contentDescription = stringResource(R.string.movie_poster),
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2f / 3f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = movieDetails.title,
                    style = MaterialTheme.typography.headlineSmall
                )

                movieDetails.tagline?.let { tagline ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$tagline\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f", movieDetails.voteAverage),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = " (${NumberFormat.getNumberInstance(Locale.US).format(movieDetails.voteCount)} ${stringResource(R.string.votes)})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (movieDetails.genres.isNotEmpty()) {
                    Text(
                        text = movieDetails.genres.joinToString(" | ") { it.name },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
