package com.poisonedyouth

import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class AddressRepository {
    fun createNewAddress(address: Address) =
        AddressEntity.new(address.id) {
            street = address.street
            number = address.number
            zipCode = address.zipCode
            city = address.city
            country = address.country
        }.id.value

    fun getAddressById(id: Long) = AddressEntity.findById(id)?.let {
        Address(
            id = it.id.value,
            street = it.street,
            number = it.number,
            zipCode = it.zipCode,
            city = it.city,
            country = it.country
        )
    }

    fun findAddressByZipCode(zipCode: Int) = AddressEntity.findAddressesByZipCode(zipCode)?.let {
        Address(
            id = it.id.value,
            street = it.street,
            number = it.number,
            zipCode = it.zipCode,
            city = it.city,
            country = it.country
        )
    }

    fun findAll() = transaction {
        AddressEntity.all().map {
            Address(
                id = it.id.value,
                street = it.street,
                number = it.number,
                zipCode = it.zipCode,
                city = it.city,
                country = it.country
            )
        }
    }
}

data class Address(
    var id: Long? = null,
    val street: String,
    val number: String,
    val zipCode: Int,
    val city: String,
    val country: String
){
    init {
        require(zipCode in 10000..99999) {
            "ZipCode must contain 5 digits!"
        }

        require(Locale.getISOCountries().contains(country)) {
            "Country '$country' must be ISO conform!"
        }
    }
}