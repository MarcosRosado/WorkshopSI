package com.example.workshopsi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
 * Composable que exibe o conteúdo detalhado de um Pokémon.
 *
 * @param pokemon O objeto [Pokemon] contendo os detalhes a serem exibidos.
 */
@Composable
fun PokemonDetailContent(pokemon: Pokemon) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(pokemon.sprites.other?.officialArtwork?.front_default ?: pokemon.sprites.front_default) // Preferir arte oficial
                .crossfade(true)
                .build(),
            contentDescription = "Imagem de ${pokemon.name}",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Altura: ${pokemon.height / 10.0} m")
        Text(text = "Peso: ${pokemon.weight / 10.0} kg")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tipos: ${pokemon.types.joinToString { it.type.name.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }}")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Estatísticas:", style = MaterialTheme.typography.titleMedium)
        pokemon.stats.forEach { statEntry ->
            Text("${statEntry.stat.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}: ${statEntry.base_stat}")
        }
    }
}