package com.example.workshopsi.ui.views.pokemonlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workshopsi.core.models.PokemonResult // Changed import
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

    private val _pokemonList = MutableStateFlow<List<PokemonResult>>(emptyList()) // Changed type
    val pokemonList: StateFlow<List<PokemonResult>> = _pokemonList.asStateFlow() // Changed type

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        getPokemonList()
    }

    fun getPokemonList(limit: Int = 151, offset: Int = 0) { // Default to showing first 151 Pokemon
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getPokemonList(limit, offset)
                _pokemonList.value = response.results // This should now align
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
