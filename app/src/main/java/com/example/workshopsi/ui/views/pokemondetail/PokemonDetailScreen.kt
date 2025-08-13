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
import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.ui.components.PokemonDetailContent

/**
 * Tela que exibe os detalhes de um Pokémon específico.
 *
 * @param navController O controlador de navegação para gerenciar as transições de tela.
 * @param pokemonName O nome do Pokémon cujos detalhes devem ser exibidos. Pode ser nulo.
 * @param viewModel O ViewModel responsável por buscar e gerenciar os dados do Pokémon.
 */
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

    // Efeito para buscar os detalhes do Pokémon quando o nome do Pokémon muda.
    LaunchedEffect(pokemonName) {
        pokemonName?.let {
            viewModel.getPokemonDetail(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemonDetail?.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "Detalhe") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        /**
         * Contêiner principal para exibir o conteúdo da tela de detalhes.
         * Gerencia a exibição com base nos estados de carregamento, erro ou sucesso.
         */
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
                        text = "Erro: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                pokemonDetail != null -> {
                    PokemonDetailContent(pokemon = pokemonDetail!!)
                }
                else -> {
                     // Isso não deve acontecer se a navegação estiver configurada corretamente
                     if (pokemonName == null) {
                         Text("Nome do Pokémon não fornecido", modifier = Modifier.align(Alignment.Center))
                     }
                }
            }
        }
    }
}
