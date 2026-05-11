package com.example.lab09

data class UserModel(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val address: AddressModel,
    val company: CompanyModel
)

data class AddressModel(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String
)

data class CompanyModel(
    val name: String,
    val catchPhrase: String,
    val bs: String
)