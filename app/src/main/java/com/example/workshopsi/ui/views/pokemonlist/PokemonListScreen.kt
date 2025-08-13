package com.example.workshopsi.ui.views.pokemonlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.ui.components.PokemonListItemCard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Exibe uma lista de Pokémon com funcionalidade de busca e rolagem infinita.
 *
 * Esta tela permite aos usuários visualizar uma lista de Pokémon, pesquisar Pokémon pelo nome
 * e carregar mais Pokémon conforme rolam a lista para baixo. Ela lida com diferentes estados
 * como carregamento, erro, lista vazia e resultados de busca.
 *
 * @param viewModel O [PokemonListViewModel] usado para gerenciar o estado e a lógica desta tela.
 * @param onPokemonClick Callback invocado quando um item Pokémon é clicado, passando o nome do Pokémon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = hiltViewModel(),
    onPokemonClick: (String) -> Unit
) {
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val canLoadMore by viewModel.canLoadMore.collectAsState()


    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokedex") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    label = { Text("Pesquisar Pokémon pelo nome") },
                    modifier = Modifier
                        .weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.onSearchConfirmed()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onSearchConfirmed() }) {
                            Icon(Icons.Filled.Search, contentDescription = "Pesquisar")
                        }
                    }
                )

            }

            /**
             * Bloco Composable responsável por exibir o conteúdo principal com base no estado atual.
             * Ele lida com a exibição de:
             * - Um carregador inicial em tela cheia.
             * - Uma mensagem de erro se ocorrer um erro.
             * - A lista de Pokémon, se disponível.
             * - Um indicador de carregamento no final da lista para paginação.
             * - Uma mensagem de estado vazio se nenhum Pokémon for encontrado ou disponível.
             */
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                when {
                    isLoading && pokemonList.isEmpty() && error == null -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    error != null -> {
                        Text(
                            text = "Erro: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                    pokemonList.isNotEmpty() -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(pokemonList, key = { pokemon -> pokemon.id }) { pokemon ->
                                PokemonListItemCard(
                                    pokemon = pokemon,
                                    onItemClick = {
                                        onPokemonClick(pokemon.name)
                                    }
                                )
                            }

                            if (isLoadingMore && canLoadMore && pokemonList.isNotEmpty()) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                    !isLoading && pokemonList.isEmpty() && error == null -> {
                        Text(
                            text = if (viewModel.isSearchActive()) "Nenhum Pokémon encontrado para \"${searchQuery}\"." else "Nenhum Pokémon disponível.",
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    /**
     * LaunchedEffect para implementar a rolagem infinita.
     * Observa o estado da rolagem e aciona [PokemonListViewModel.loadMorePokemon]
     * quando o usuário rola para perto do final da lista, desde que mais itens possam ser carregados
     * e nenhuma pesquisa esteja ativa.
     */
    LaunchedEffect(listState, isLoading, isLoadingMore, canLoadMore, pokemonList.size, viewModel.isSearchActive()) {
        if (isLoading || isLoadingMore || !canLoadMore || pokemonList.isEmpty() || viewModel.isSearchActive()) {
            return@LaunchedEffect
        }

        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (visibleItemsInfo.isEmpty()) {
                    false
                } else {
                    val lastVisibleItemIndex = visibleItemsInfo.last().index
                    val threshold = 5
                    lastVisibleItemIndex >= pokemonList.size - 1 - threshold
                }
            }
            .distinctUntilChanged()
            .filter { shouldLoad -> shouldLoad }
            .collect {
                if (!isLoading && !isLoadingMore && canLoadMore && !viewModel.isSearchActive()) {
                    viewModel.loadMorePokemon()
                }
            }
    }
}
