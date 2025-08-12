package com.example.workshopsi.ui.views.pokemondetail

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
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonDetail = MutableStateFlow<Pokemon?>(null)
    val pokemonDetail: StateFlow<Pokemon?> = _pokemonDetail.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getPokemonDetail(pokemonName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val detail = repository.getPokemonDetail(pokemonName)
                _pokemonDetail.value = detail
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
