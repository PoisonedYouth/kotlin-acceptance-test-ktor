package com.poisonedyouth

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction

class CustomerEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CustomerEntity>(CustomerTable) {
        override fun findById(id: EntityID<Long>): CustomerEntity? = transaction {
            super.findById(id)
        }

        fun deleteCustomer(customerEntity: CustomerEntity) = transaction {
            AccountTable.deleteWhere { AccountTable.customer eq customerEntity.id }
            CustomerTable.deleteWhere { CustomerTable.id eq customerEntity.id }
        }

        override fun new(id: Long?, init: CustomerEntity.() -> Unit): CustomerEntity = transaction {
            super.new(id, init)
        }

        fun findByFirstNameAndLastName(firstName: String, lastName: String) = transaction {
            CustomerEntity.find { (CustomerTable.firstName eq firstName) and (CustomerTable.lastName eq lastName) }
                .singleOrNull()
        }

        fun existsCustomerByEmail(email: String) = transaction {
            CustomerEntity.find { CustomerTable.email eq email }.singleOrNull() != null
        }
    }

    var firstName by CustomerTable.firstName
    var lastName by CustomerTable.lastName
    var birthDate by CustomerTable.birthDate
    var email by CustomerTable.email
    var addressEntity by AddressEntity referencedOn CustomerTable.address
    val accounts by AccountEntity referrersOn AccountTable.customer

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomerEntity

        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (birthDate != other.birthDate) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + birthDate.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}

internal object CustomerTable : LongIdTable("customer", "id") {
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val birthDate = date("birth_date")
    val email = varchar("email", 255).uniqueIndex()
    val address = reference("address_id", AddressTable)
}