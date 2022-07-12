package com.poisonedyouth

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CleanDatabaseExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"
            driverClassName = "org.h2.Driver"
            username = "root"
            password = "password"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        val database = Database.connect(HikariDataSource(hikariConfig))
        transaction(database) {
            SchemaUtils.drop(AddressTable, AccountTable, CustomerTable)
            SchemaUtils.create(AddressTable, CustomerTable, AccountTable)
        }
    }
}