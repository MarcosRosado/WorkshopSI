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

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _canLoadMore = MutableStateFlow(true)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 20

    private var allPaginatedPokemon: MutableList<Pokemon> = mutableListOf()
    private var lastKnownApiCanLoadMore: Boolean = true

    // Tracks if the current list is a result of a search operation
    private val _isSearchActive = MutableStateFlow(false)
    fun isSearchActive(): Boolean = _isSearchActive.value

    init {
        loadInitialPokemonPage()
    }

    private fun loadInitialPokemonPage() {
        if (allPaginatedPokemon.isNotEmpty() && currentOffset > 0 && !_isLoading.value && !_isLoadingMore.value) {
            return
        }
        currentOffset = 0
        allPaginatedPokemon.clear()
        _isSearchActive.value = false // Reset search status
        fetchPokemonPage(isInitialLoad = true)
    }

    fun loadMorePokemon() {
        if (_isLoading.value || _isLoadingMore.value || !_canLoadMore.value || _searchQuery.value.isNotBlank() && _isSearchActive.value) {
            return
        }
        fetchPokemonPage(isInitialLoad = false)
    }

    private fun fetchPokemonPage(isInitialLoad: Boolean) {
        if (_isSearchActive.value) return // Don't paginate if a search is active

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
                _error.value = e.message ?: "Failed to load Pokémon. Please try again."
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

    private fun searchPokemonByNameOnApi(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // Since search results are not paginated, we typically wouldn't load more.
            // However, _canLoadMore might be used by UI to show/hide pagination indicators.
            // For search, explicitly set it to false.
            _canLoadMore.value = false 

            try {
                val pokemon = repository.getPokemonDetail(name.trim())
                if (pokemon != null) {
                    _pokemonList.value = listOf(pokemon)
                } else {
                    _pokemonList.value = emptyList()
                    _error.value = "Pokémon '$name' not found."
                }
            } catch (e: Exception) {
                _pokemonList.value = emptyList()
                _error.value = "Failed to search for Pokémon '$name'. API might be down or name is incorrect."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // If the query is cleared, revert to the paginated list
        if (query.isBlank() && _isSearchActive.value) {
            _isSearchActive.value = false
            _pokemonList.value = allPaginatedPokemon.toList()
            _error.value = null
            _canLoadMore.value = lastKnownApiCanLoadMore
            // If list became empty after clearing search (e.g. initial load failed, then search, then clear)
            // and we can load more, try fetching initial page again.
            if (allPaginatedPokemon.isEmpty() && lastKnownApiCanLoadMore) {
                 loadInitialPokemonPage() // This will also set _isSearchActive to false
            } else {
                 _isLoading.value = false
            }
        }
    }

    fun onSearchConfirmed() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            // If search is confirmed with a blank query, effectively it means to clear the search.
            if (_isSearchActive.value) { // only revert if a search was active
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
