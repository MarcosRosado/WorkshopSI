package com.example.workshopsi.ui.views.pokemonlist

import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // ktlint-disable no-wildcard-imports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.workshopsi.core.models.PokemonResult
import com.example.workshopsi.ui.components.PokemonListItemCard // Added import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel(),
    onPokemonClick: (String) -> Unit
) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokedex") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && pokemonList.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                pokemonList.isNotEmpty() -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pokemonList) { pokemonResult ->
                            PokemonListItemCard(pokemonResult = pokemonResult, onItemClick = {
                                onPokemonClick(pokemonResult.name)
                            })
                        }
                    }
                }
                else -> {
                     Text("No Pokémon found.", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
