package com.amro.movies.feature.trending.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.amro.movies.feature.trending.R
import com.amro.movies.feature.trending.SortOption
import com.amro.movies.feature.trending.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortOption: SortOption,
    currentSortOrder: SortOrder,
    onSortOptionSelected: (SortOption) -> Unit,
    onSortOrderSelected: (SortOrder) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    var tempSortOption by remember(currentSortOption) { mutableStateOf(currentSortOption) }
    var tempSortOrder by remember(currentSortOrder) { mutableStateOf(currentSortOrder) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(Modifier.selectableGroup()) {
                SortOptionItem(
                    label = stringResource(R.string.popularity),
                    isSelected = tempSortOption == SortOption.POPULARITY,
                    onClick = { tempSortOption = SortOption.POPULARITY }
                )
                SortOptionItem(
                    label = stringResource(R.string.title),
                    isSelected = tempSortOption == SortOption.TITLE,
                    onClick = { tempSortOption = SortOption.TITLE }
                )
                SortOptionItem(
                    label = stringResource(R.string.release_date),
                    isSelected = tempSortOption == SortOption.RELEASE_DATE,
                    onClick = { tempSortOption = SortOption.RELEASE_DATE }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Order",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = tempSortOrder == SortOrder.DESCENDING,
                    onClick = { tempSortOrder = SortOrder.DESCENDING },
                    label = { Text(stringResource(R.string.descending)) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = tempSortOrder == SortOrder.ASCENDING,
                    onClick = { tempSortOrder = SortOrder.ASCENDING },
                    label = { Text(stringResource(R.string.ascending)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onSortOptionSelected(tempSortOption)
                    onSortOrderSelected(tempSortOrder)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    }
}

@Composable
private fun SortOptionItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
