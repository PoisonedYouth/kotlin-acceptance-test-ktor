package com.poisonedyouth

data class CustomerDto(
    val firstName: String,
    val lastName: String,
    val birthdate: String,
    val email: String,
    val address: AddressDto,
    val accounts: Set<AccountDto>
)

data class AddressDto(
    val street: String,
    val number: String,
    val city: String,
    val zipCode: Int,
    val country: String
)

data class AccountDto(
    val number: Int,
    val balance: Int
)