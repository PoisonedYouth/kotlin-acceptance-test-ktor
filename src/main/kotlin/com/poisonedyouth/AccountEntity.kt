package com.poisonedyouth

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction


class AccountEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AccountEntity>(AccountTable){
        override fun all(): SizedIterable<AccountEntity>  = transaction{
            super.all()
        }
        override fun new(id: Long?, init: AccountEntity.() -> Unit): AccountEntity = transaction{
            super.new(id, init)
        }
    }

    var number by AccountTable.number
    var balance by AccountTable.balance
    var customerEntity by CustomerEntity referencedOn AccountTable.customer
}

internal object AccountTable : LongIdTable(name = "account", columnName = "id") {
    val number = integer("number")
    val balance = integer("balance")
    val customer = reference("customer", CustomerTable)
}