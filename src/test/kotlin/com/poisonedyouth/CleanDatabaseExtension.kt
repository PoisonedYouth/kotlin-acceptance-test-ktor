package com.poisonedyouth

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

private class KMySQLContainer(image: DockerImageName) : MySQLContainer<KMySQLContainer>(image)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CleanDatabaseExtension : BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = container.jdbcUrl
            driverClassName = container.driverClassName
            username = container.username
            password = container.password
            validationTimeout = 60000
            validate()
        }
        val database = Database.connect(HikariDataSource(hikariConfig))
        transaction(database) {
            SchemaUtils.drop(AddressTable, AccountTable, CustomerTable)
            SchemaUtils.create(AddressTable, CustomerTable, AccountTable)
        }
    }

    companion object {
        private val container = DatabaseContainer.mySQLContainer
    }
}

private object DatabaseContainer {

    val mySQLContainer: KMySQLContainer = KMySQLContainer(
        DockerImageName.parse("mysql:5.7")

    ).apply {
        withUsername("root")
        withPassword("password")
    }

    init {
        mySQLContainer.start()
    }
}