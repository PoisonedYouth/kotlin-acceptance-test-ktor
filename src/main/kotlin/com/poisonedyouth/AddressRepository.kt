package com.poisonedyouth

class AddressRepository {
    fun createNewAddress(address: Address) =
        AddressEntity.new {
            street = address.street
            number = address.number
            zipCode = address.zipCode
            city = address.city
            country = address.country
        }.id.value

    fun getAddressById(id: Long) = AddressEntity.findById(id)?.let {
        Address(
            street = it.street,
            number = it.number,
            zipCode = it.zipCode,
            city = it.city,
            country = it.country)
    }
}

data class Address(
    val street: String,
    val number: String,
    val zipCode: Int,
    val city: String,
    val country: String
)