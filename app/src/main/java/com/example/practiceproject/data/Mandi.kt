package com.example.practiceproject.data

data class Mandi(
    val registered_loyality_index : String,
    val unregistered_loyality_index : String,
    val villages : List<Village>,
    val sellers : List<Seller>
)
