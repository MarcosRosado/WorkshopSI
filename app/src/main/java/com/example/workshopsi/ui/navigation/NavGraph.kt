package com.example.workshopsi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.workshopsi.ui.views.pokemondetail.PokemonDetailScreen
import com.example.workshopsi.ui.views.pokemonlist.PokemonListScreen

object Routes {
    const val POKEMON_LIST = "pokemon_list"
    const val POKEMON_DETAIL = "pokemon_detail/{pokemonName}"

    fun pokemonDetail(pokemonName: String) = "pokemon_detail/$pokemonName"
}

@Composable
fun NavGraph(
    startDestination: String = Routes.POKEMON_LIST
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.POKEMON_LIST) {
            PokemonListScreen(
                // viewModel is hiltViewModel() by default in PokemonListScreen
                onPokemonClick = { pokemonName ->
                    navController.navigate(Routes.pokemonDetail(pokemonName))
                }
            )
        }
        composable(
            route = Routes.POKEMON_DETAIL,
            arguments = listOf(navArgument("pokemonName") { type = NavType.StringType })
        ) { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName")
            // pokemonName can be null if the argument is not passed correctly or is optional and missing
            // PokemonDetailScreen already handles pokemonName being null internally.
            PokemonDetailScreen(
                navController = navController,
                pokemonName = pokemonName
            )
        }
    }
}
