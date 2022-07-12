package com.poisonedyouth

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.flushCache
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction


class Account(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Account>(AccountTable){
        override fun all(): SizedIterable<Account>  = transaction{
            super.all()
        }
        override fun new(id: Long?, init: Account.() -> Unit): Account = transaction{
            super.new(id, init)
        }
    }

    var number by AccountTable.number
    var balance by AccountTable.balance
    var customer by Customer referencedOn AccountTable.customer
}

internal object AccountTable : LongIdTable(name = "account", columnName = "id") {
    val number = integer("number")
    val balance = integer("balance")
    val customer = reference("customer", CustomerTable)
}