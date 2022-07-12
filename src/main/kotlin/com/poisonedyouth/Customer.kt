package com.poisonedyouth

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction

class Customer(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Customer>(CustomerTable) {
        override fun findById(id: EntityID<Long>): Customer? = transaction {
            super.findById(id)
        }

        fun deleteCustomer(customer: Customer) = transaction {
            AccountTable.deleteWhere{AccountTable.customer eq customer.id}
            CustomerTable.deleteWhere { CustomerTable.id eq customer.id }
        }

        override fun new(id: Long?, init: Customer.() -> Unit): Customer = transaction{
            super.new(id, init)
        }
    }

    var firstName by CustomerTable.firstName
    var lastName by CustomerTable.lastName
    var birthDate by CustomerTable.birthDate
    var email by CustomerTable.email
    var address by Address referencedOn CustomerTable.address
    val accounts by Account referrersOn AccountTable.customer

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Customer

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