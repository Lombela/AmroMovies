package com.amro.movies.feature.detail.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amro.movies.feature.detail.R
import com.amro.movies.domain.model.MovieDetails
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MovieInfo(
    movieDetails: MovieDetails,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.padding(16.dp)) {
        // Overview Section
        movieDetails.overview?.let { overview ->
            Text(
                text = stringResource(R.string.overview),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = stringResource(R.string.details),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Status
                movieDetails.status?.let { status ->
                    DetailRow(
                        label = stringResource(R.string.status),
                        value = status
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                movieDetails.releaseDate?.let { date ->
                    DetailRow(
                        label = stringResource(R.string.release_date),
                        value = date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                movieDetails.runtime?.let { runtime ->
                    DetailRow(
                        label = stringResource(R.string.runtime),
                        value = formatRuntime(runtime)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                if (movieDetails.budget > 0) {
                    DetailRow(
                        label = stringResource(R.string.budget),
                        value = formatCurrency(movieDetails.budget)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                if (movieDetails.revenue > 0) {
                    DetailRow(
                        label = stringResource(R.string.revenue),
                        value = formatCurrency(movieDetails.revenue)
                    )
                }
            }
        }

        movieDetails.imdbId?.let { imdbId ->
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$imdbId"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.view_on_imdb))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatRuntime(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}

private fun formatCurrency(amount: Long): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}
