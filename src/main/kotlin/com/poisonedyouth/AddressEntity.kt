package com.poisonedyouth

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction


class AddressEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AddressEntity>(AddressTable) {
        fun findAddressesByZipCode(zipCode: Int) = transaction {
            AddressEntity.find { AddressTable.zipCode eq zipCode }.singleOrNull()
        }

        override fun all(): SizedIterable<AddressEntity> = transaction {
            super.all()
        }

        override fun findById(id: EntityID<Long>): AddressEntity? = transaction {
            super.findById(id)
        }

        override fun new(id: Long?, init: AddressEntity.() -> Unit): AddressEntity = transaction {
            super.new(id, init)
        }
    }

    var street by AddressTable.street
    var number by AddressTable.number
    var zipCode by AddressTable.zipCode
    var city by AddressTable.city
    var country by AddressTable.country

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddressEntity

        if (id != other.id) return false
        if (street != other.street) return false
        if (number != other.number) return false
        if (zipCode != other.zipCode) return false
        if (city != other.city) return false
        if (country != other.country) return false

        return true
    }

    override fun hashCode(): Int {
        var result = street.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + zipCode
        result = 31 * result + city.hashCode()
        result = 31 * result + country.hashCode()
        return result
    }

}

internal object AddressTable : LongIdTable("address", "id") {
    val street = varchar("street", 255)
    val number = varchar("number", 255)
    val zipCode = integer("zip_code")
    val city = varchar("city", 255)
    val country = varchar("country", 255)
}