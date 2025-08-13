package com.example.workshopsi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.workshopsi.core.models.Pokemon

/**
 * Um card que exibe um item da lista de Pokémon.
 *
 * @param pokemon O objeto [Pokemon] a ser exibido.
 * @param onItemClick A ação a ser executada quando o card é clicado.
 */
@Composable
fun PokemonListItemCard(
    pokemon: Pokemon,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemon.sprites.other?.officialArtwork?.front_default ?: pokemon.sprites.front_default)
                    .crossfade(true)
                    .build(),
                contentDescription = pokemon.name + " sprite", // Mantido em inglês para possível uso programático, ou poderia ser "Sprite de " + pokemon.name
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tipo(s): " + pokemon.types.joinToString { it.type.name.replaceFirstChar(Char::titlecase) },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
