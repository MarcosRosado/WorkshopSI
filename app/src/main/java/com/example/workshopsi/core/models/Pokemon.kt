package com.example.workshopsi.core.models

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: PokemonSprites,
    val types: List<PokemonTypeEntry>,
    val stats: List<PokemonStatEntry>
)

data class PokemonSprites(
    val front_default: String?,
    @SerializedName("other") val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    val front_default: String?
)

data class PokemonTypeEntry(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String,
    val url: String
)

data class PokemonStatEntry(
    val base_stat: Int,
    val effort: Int,
    val stat: PokemonStat
)

data class PokemonStat(
    val name: String,
    val url: String
)
