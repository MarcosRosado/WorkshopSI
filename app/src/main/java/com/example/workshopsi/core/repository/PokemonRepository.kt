package com.example.workshopsi.core.repository

import com.example.workshopsi.core.models.Pokemon
import com.example.workshopsi.core.models.PokemonListResponse
import com.example.workshopsi.core.services.PokeApiService
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val pokeApiService: PokeApiService
) {

    suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse {
        return pokeApiService.getPokemonList(limit, offset)
    }

    suspend fun getPokemonDetail(pokemonName: String): Pokemon {
        return pokeApiService.getPokemonDetail(pokemonName)
    }
}
