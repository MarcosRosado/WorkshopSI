package com.example.workshopsi.ui.views.pokemondetail

import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.workshopsi.core.models.Pokemon // Assuming this is your Pokemon model

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    navController: NavController,
    pokemonName: String?,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val pokemonDetail by viewModel.pokemonDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(pokemonName) {
        pokemonName?.let {
            viewModel.getPokemonDetail(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemonDetail?.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = "Error: $error", // Fixed redundant curly braces
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                pokemonDetail != null -> {
                    PokemonDetailContent(pokemon = pokemonDetail!!)
                }
                else -> {
                     if (pokemonName == null) { // Should not happen if navigation is set up correctly
                         Text("Pokemon name not provided", modifier = Modifier.align(Alignment.Center))
                     }
                }
            }
        }
    }
}

@Composable
fun PokemonDetailContent(pokemon: Pokemon) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(pokemon.sprites.other?.officialArtwork?.front_default ?: pokemon.sprites.front_default) // Prefer official artwork
                .crossfade(true)
                .build(),
            contentDescription = "${pokemon.name} image",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pokemon.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Height: ${pokemon.height / 10.0} m")
        Text(text = "Weight: ${pokemon.weight / 10.0} kg")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Types: ${pokemon.types.joinToString { it.type.name.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }}")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Stats:", style = MaterialTheme.typography.titleMedium)
        pokemon.stats.forEach { statEntry ->
            Text("${statEntry.stat.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}: ${statEntry.base_stat}")
        }
    }
}
