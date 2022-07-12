package com.poisonedyouth

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AddressTest {


    @BeforeEach
    fun setupDatasource() {
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
            SchemaUtils.drop(AddressTable)
            SchemaUtils.create(AddressTable)
        }
    }

    @Test
    fun `save address is possible`() {
        // given + when
        val actual = Address.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }


        // then
        assertThat(Address.findById(actual.id)).isEqualTo(actual)
    }

    @Test
    fun `findAddressesByZipCode returns matching address`() {
        // given
        Address.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }


        val address2 = Address.new {
            street = "Hauptstrasse"
            number = "25A"
            zipCode = 10115
            city = "Berlin"
            country = "DE"
        }

        // when
        val actual = Address.findAddressesByZipCode(10115)

        // then
        assertThat(actual).isEqualTo(address2)
    }

    @Test
    fun `findAddressesByCity returns empty optional for no matching address`() {
        // given
        Address.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        Address.new {
            street = "Hauptstrasse"
            number = "25A"
            zipCode = 10115
            city = "Berlin"
            country = "DE"
        }

        // when
        val actual = Address.findAddressesByZipCode(88888)

        // then
        assertThat(actual).isNull()
    }
}