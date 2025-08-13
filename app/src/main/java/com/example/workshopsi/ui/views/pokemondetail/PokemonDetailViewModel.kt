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

/**
 * ViewModel para a tela de detalhes do Pokémon.
 * Responsável por buscar e gerenciar o estado dos detalhes de um Pokémon específico.
 *
 * @property repository Repositório para buscar dados dos Pokémon.
 */
@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    /**
     * Fluxo que emite os detalhes do Pokémon atualmente carregado.
     * Null se nenhum Pokémon foi carregado ou se ocorreu um erro.
     */
    private val _pokemonDetail = MutableStateFlow<Pokemon?>(null)
    val pokemonDetail: StateFlow<Pokemon?> = _pokemonDetail.asStateFlow()

    /**
     * Fluxo que indica se os detalhes do Pokémon estão sendo carregados.
     * True se uma operação de carregamento está em andamento, false caso contrário.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Fluxo que emite mensagens de erro que podem ocorrer durante a busca dos detalhes do Pokémon.
     * Null se não houver erro.
     */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Busca os detalhes de um Pokémon pelo nome.
     * Atualiza os fluxos [_pokemonDetail], [_isLoading] e [_error] com base no resultado da operação.
     *
     * @param pokemonName O nome do Pokémon a ser buscado.
     */
    fun getPokemonDetail(pokemonName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val detail = repository.getPokemonDetail(pokemonName)
                _pokemonDetail.value = detail
            } catch (e: Exception) {
                // Em uma aplicação real, seria ideal ter mensagens de erro mais específicas ou códigos de erro.
                _error.value = e.message ?: "Um erro desconhecido ocorreu"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
