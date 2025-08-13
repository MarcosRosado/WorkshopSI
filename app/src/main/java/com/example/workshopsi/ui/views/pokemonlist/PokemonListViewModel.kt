package com.example.workshopsi.ui.views.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.core.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela de lista de Pokémon.
 * Gerencia o estado da UI e interage com o [PokemonRepository] para buscar dados dos Pokémon.
 *
 * @property repository O repositório para buscar dados dos Pokémon.
 */
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    /** Fluxo de estado para a lista de Pokémon a ser exibida. */
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    /** Fluxo de estado para indicar se a carga inicial está em progresso. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    /** Fluxo de estado para indicar se mais Pokémon estão sendo carregados (paginação). */
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    /** Fluxo de estado para mensagens de erro. */
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    /** Fluxo de estado para a consulta de pesquisa atual. */
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _canLoadMore = MutableStateFlow(true)
    /** Fluxo de estado que indica se mais Pokémon podem ser carregados da API (paginação). */
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 20

    private var allPaginatedPokemon: MutableList<Pokemon> = mutableListOf()
    private var lastKnownApiCanLoadMore: Boolean = true

    // Rastreia se a lista atual é resultado de uma operação de busca
    private val _isSearchActive = MutableStateFlow(false)
    /** Retorna `true` se uma busca estiver ativa, `false` caso contrário. */
    fun isSearchActive(): Boolean = _isSearchActive.value

    init {
        loadInitialPokemonPage()
    }

    /**
     * Carrega a página inicial de Pokémon.
     * Limpa a lista existente e busca a primeira página.
     * Define `_isSearchActive` como falso.
     */
    private fun loadInitialPokemonPage() {
        if (allPaginatedPokemon.isNotEmpty() && currentOffset > 0 && !_isLoading.value && !_isLoadingMore.value) {
            return
        }
        currentOffset = 0
        allPaginatedPokemon.clear()
        _isSearchActive.value = false // Reseta o status da busca
        fetchPokemonPage(isInitialLoad = true)
    }

    /**
     * Carrega mais Pokémon se não houver uma carga em andamento,
     * se houver mais Pokémon para carregar e se uma busca não estiver ativa.
     */
    fun loadMorePokemon() {
        if (_isLoading.value || _isLoadingMore.value || !_canLoadMore.value || (_searchQuery.value.isNotBlank() && _isSearchActive.value)) {
            return
        }
        fetchPokemonPage(isInitialLoad = false)
    }

    /**
     * Busca uma página de Pokémon do repositório.
     *
     * @param isInitialLoad Indica se esta é a carga inicial.
     */
    private fun fetchPokemonPage(isInitialLoad: Boolean) {
        if (_isSearchActive.value) return // Não pagina se uma busca estiver ativa

        viewModelScope.launch {
            if (isInitialLoad) {
                _isLoading.value = true
                if (allPaginatedPokemon.isEmpty()) _pokemonList.value = emptyList()
            } else {
                _isLoadingMore.value = true
            }
            _error.value = null

            try {
                val (newPageDetailedPokemon, canLoadMoreFromApi) = repository.getPaginatedPokemonWithDetails(
                    limit = pageSize,
                    offset = currentOffset
                )

                if (newPageDetailedPokemon.isNotEmpty()) {
                    if (isInitialLoad) {
                        allPaginatedPokemon.clear()
                    }
                    allPaginatedPokemon.addAll(newPageDetailedPokemon)
                    _pokemonList.value = allPaginatedPokemon.toList()
                    currentOffset += newPageDetailedPokemon.size
                } else if (isInitialLoad) {
                    _pokemonList.value = emptyList()
                }
                _canLoadMore.value = canLoadMoreFromApi && newPageDetailedPokemon.isNotEmpty()
                lastKnownApiCanLoadMore = _canLoadMore.value

            } catch (e: Exception) {
                _error.value = e.message ?: "Falha ao carregar Pokémon. Por favor, tente novamente."
                if (isInitialLoad) {
                    allPaginatedPokemon.clear()
                    _pokemonList.value = emptyList()
                }
                _canLoadMore.value = false
                lastKnownApiCanLoadMore = false
            } finally {
                if (isInitialLoad) {
                    _isLoading.value = false
                } else {
                    _isLoadingMore.value = false
                }
            }
        }
    }

    /**
     * Busca um Pokémon pelo nome na API.
     * Os resultados não são paginados e `_canLoadMore` é definido como falso.
     *
     * @param name O nome do Pokémon a ser buscado.
     */
    private fun searchPokemonByNameOnApi(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // Como os resultados da busca não são paginados, normalmente não carregaríamos mais.
            // No entanto, _canLoadMore pode ser usado pela UI para mostrar/ocultar indicadores de paginação.
            // Para a busca, defina explicitamente como falso.
            _canLoadMore.value = false

            try {
                val pokemon = repository.getPokemonDetail(name.trim())
                if (pokemon != null) {
                    _pokemonList.value = listOf(pokemon)
                } else {
                    _pokemonList.value = emptyList()
                    _error.value = "Pokémon '$name' não encontrado."
                }
            } catch (e: Exception) {
                _pokemonList.value = emptyList()
                _error.value = "Falha ao buscar por Pokémon '$name'. A API pode estar indisponível ou o nome está incorreto."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Chamado quando a consulta de pesquisa é alterada na UI.
     * Atualiza `_searchQuery`. Se a consulta ficar em branco e uma busca estava ativa,
     * reverte para a lista paginada e reseta o estado da busca.
     *
     * @param query A nova consulta de pesquisa.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // Se a consulta for limpa, reverte para a lista paginada
        if (query.isBlank() && _isSearchActive.value) {
            _isSearchActive.value = false
            _pokemonList.value = allPaginatedPokemon.toList()
            _error.value = null
            _canLoadMore.value = lastKnownApiCanLoadMore
            // Se a lista ficou vazia após limpar a busca (ex: carga inicial falhou, depois busca, depois limpa)
            // e podemos carregar mais, tenta buscar a página inicial novamente.
            if (allPaginatedPokemon.isEmpty() && lastKnownApiCanLoadMore) {
                 loadInitialPokemonPage() // Isso também definirá _isSearchActive como falso
            } else {
                 _isLoading.value = false
            }
        }
    }

    /**
     * Chamado quando a busca é confirmada (ex: pressionando o botão de busca ou o enter do teclado).
     * Se a consulta não estiver em branco, ativa o modo de busca e busca o Pokémon.
     * Se a consulta estiver em branco e uma busca estava ativa, limpa o estado da busca.
     */
    fun onSearchConfirmed() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            // Se a busca for confirmada com uma consulta em branco, efetivamente significa limpar a busca.
            if (_isSearchActive.value) { // só reverte se uma busca estava ativa
                 _isSearchActive.value = false
                _pokemonList.value = allPaginatedPokemon.toList()
                _error.value = null
                _canLoadMore.value = lastKnownApiCanLoadMore
                if (allPaginatedPokemon.isEmpty() && lastKnownApiCanLoadMore) {
                    loadInitialPokemonPage()
                } else {
                     _isLoading.value = false
                }
            }
            return
        }
        _isSearchActive.value = true
        searchPokemonByNameOnApi(query)
    }
}
