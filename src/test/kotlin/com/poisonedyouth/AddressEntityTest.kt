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
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension


@ExtendWith(CleanDatabaseExtension::class)
internal class AddressEntityTest {
    @Test
    fun `save address is possible`() {
        // given + when
        val actual = AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }


        // then
        assertThat(AddressEntity.findById(actual.id)).isEqualTo(actual)
    }

    @Test
    fun `findAddressesByZipCode returns matching address`() {
        // given
        AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }


        val addressEntity2 = AddressEntity.new {
            street = "Hauptstrasse"
            number = "25A"
            zipCode = 10115
            city = "Berlin"
            country = "DE"
        }

        // when
        val actual = AddressEntity.findAddressesByZipCode(10115)

        // then
        assertThat(actual).isEqualTo(addressEntity2)
    }

    @Test
    fun `findAddressesByCity returns empty optional for no matching address`() {
        // given
        AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        AddressEntity.new {
            street = "Hauptstrasse"
            number = "25A"
            zipCode = 10115
            city = "Berlin"
            country = "DE"
        }

        // when
        val actual = AddressEntity.findAddressesByZipCode(88888)

        // then
        assertThat(actual).isNull()
    }
}