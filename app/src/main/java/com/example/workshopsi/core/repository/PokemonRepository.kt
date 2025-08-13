package com.example.workshopsi.core.repository

import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.core.models.PokemonListResponse // Keep this
import com.example.workshopsi.core.services.PokeApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val pokeApiService: PokeApiService
) {

    /**
     * Fetches a paginated list of Pokemon with their full details.
     * @param limit Number of Pokemon to fetch per page.
     * @param offset Starting index for fetching Pokemon.
     * @return Pair<List<Pokemon>, Boolean> where List<Pokemon> is the list of fetched Pokemon details,
     *         and Boolean is true if more Pokemon can be loaded (i.e., response.next is not null), false otherwise.
     */
    suspend fun getPaginatedPokemonWithDetails(limit: Int, offset: Int): Pair<List<Pokemon>, Boolean> = coroutineScope {
        // Step 1: Fetch the basic list of Pokemon (names and detail URLs) for the current page
        val listResponse = pokeApiService.getPokemonList(limit = limit, offset = offset)

        // Step 2: Fetch details for each Pokemon in the list concurrently
        val detailedPokemonDeferred = listResponse.results.map { pokemonResult ->
            async {
                // Assuming pokemonResult.name is the correct identifier for getPokemonDetail
                // If the API needs an ID extracted from pokemonResult.url, that logic would go here.
                pokeApiService.getPokemonDetail(pokemonResult.name)
            }
        }
        val detailedPokemonList = detailedPokemonDeferred.awaitAll().filterNotNull() // Filter out any nulls if a detail fetch fails

        // Step 3: Determine if more Pokemon can be loaded
        val canLoadMore = listResponse.next != null

        return@coroutineScope Pair(detailedPokemonList, canLoadMore)
    }

    // If you need a function to get a single Pokémon's details (e.g., for a detail screen or later for search by exact name)
    suspend fun getPokemonDetail(nameOrId: String): Pokemon? {
        return try {
            pokeApiService.getPokemonDetail(nameOrId)
        } catch (e: Exception) {
            // Handle error, e.g., Pokémon not found
            null
        }
    }
}
