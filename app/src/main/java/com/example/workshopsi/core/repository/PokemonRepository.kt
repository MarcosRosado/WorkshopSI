package com.example.workshopsi.core.repository

import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.core.services.PokeApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Repositório responsável por buscar dados de Pokémon, tanto da API quanto de uma possível fonte de dados local (não implementado).
 * Ele abstrai a origem dos dados para os ViewModels.
 *
 * @property pokeApiService O serviço da API para buscar dados de Pokémon.
 */
class PokemonRepository @Inject constructor(
    private val pokeApiService: PokeApiService
) {

    /**
     * Busca uma lista paginada de Pokémon com seus detalhes completos.
     *
     * @param limit Número de Pokémon para buscar por página.
     * @param offset Índice inicial para buscar Pokémon.
     * @return Pair<List<Pokemon>, Boolean> onde List<Pokemon> é a lista de detalhes dos Pokémon buscados,
     *         e Boolean é verdadeiro se mais Pokémon puderem ser carregados (ou seja, response.next não é nulo), falso caso contrário.
     */
    suspend fun getPaginatedPokemonWithDetails(limit: Int, offset: Int): Pair<List<Pokemon>, Boolean> = coroutineScope {
        // Passo 1: Busca a lista básica de Pokémon (nomes e URLs de detalhes) para a página atual
        val listResponse = pokeApiService.getPokemonList(limit = limit, offset = offset)

        // Passo 2: Busca os detalhes de cada Pokémon na lista concorrentemente
        val detailedPokemonDeferred = listResponse.results.map { pokemonResult ->
            async {
                // Supondo que pokemonResult.name é o identificador correto para getPokemonDetail
                // Se a API precisasse de um ID extraído de pokemonResult.url, essa lógica iria aqui.
                pokeApiService.getPokemonDetail(pokemonResult.name)
            }
        }
        val detailedPokemonList = detailedPokemonDeferred.awaitAll().filterNotNull() // Filtra quaisquer nulos se uma busca de detalhe falhar

        // Passo 3: Determina se mais Pokémon podem ser carregados
        val canLoadMore = listResponse.next != null

        return@coroutineScope Pair(detailedPokemonList, canLoadMore)
    }

    /**
     * Busca os detalhes de um único Pokémon pelo nome ou ID.
     * Útil para telas de detalhes ou para buscar por nome exato.
     *
     * @param nameOrId O nome ou ID do Pokémon a ser buscado.
     * @return O objeto [Pokemon] com os detalhes, ou nulo se ocorrer um erro (ex: Pokémon não encontrado).
     */
    suspend fun getPokemonDetail(nameOrId: String): Pokemon? {
        return try {
            pokeApiService.getPokemonDetail(nameOrId)
        } catch (e: Exception) {
            // Lida com o erro, ex: Pokémon não encontrado
            null
        }
    }
}
