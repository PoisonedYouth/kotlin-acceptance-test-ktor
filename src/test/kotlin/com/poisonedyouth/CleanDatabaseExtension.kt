package com.poisonedyouth

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

private class KMySQLContainer(image: DockerImageName) : MySQLContainer<KMySQLContainer>(image)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CleanDatabaseExtension : BeforeEachCallback, AfterEachCallback {


    override fun beforeEach(context: ExtensionContext?) {
        transaction(database) {
            SchemaUtils.create(AddressTable, CustomerTable, AccountTable)
        }
    }

    override fun afterEach(context: ExtensionContext?) {
        transaction(database) {
            SchemaUtils.drop(AddressTable, AccountTable, CustomerTable)
        }
    }

    companion object {
        private val database = DatabaseContainer.database
    }
}

private object DatabaseContainer {

    val mySQLContainer: KMySQLContainer = KMySQLContainer(
        DockerImageName.parse("mysql:5.7")

    ).apply {
        withUsername("root")
        withPassword("password")
    }
    val database: Database
        get() {
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = mySQLContainer.jdbcUrl
                driverClassName = mySQLContainer.driverClassName
                username = mySQLContainer.username
                password = mySQLContainer.password
                validationTimeout = 60000
                maximumPoolSize = 200
                validate()
            }
            return Database.connect(HikariDataSource(hikariConfig))
        }


    init {
        mySQLContainer.start()
    }
}