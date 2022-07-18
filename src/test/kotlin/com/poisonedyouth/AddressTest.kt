package com.poisonedyouth

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class AddressTest {

    @Test
    fun `create Address fails if zipCode is too short`() {
        // given - when - then
        Assertions.assertThatThrownBy {
            Address(
                street = "Main Street",
                number = "13",
                zipCode = 123,
                city = "Los Angeles",
                country = "US"
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("ZipCode must contain 5 digits!")
    }

    @Test
    fun `create Address fails if zipCode is too long`() {
        // given - when - then
        Assertions.assertThatThrownBy {
            Address(
                street = "Main Street",
                number = "13",
                zipCode = 123001,
                city = "Los Angeles",
                country = "US"
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("ZipCode must contain 5 digits!")
    }

    @Test
    fun `create Address fails if country is invalid`() {
        // given - when - then
        Assertions.assertThatThrownBy {
            Address(
                street = "Main Street",
                number = "13",
                zipCode = 90001,
                city = "Los Angeles",
                country = "ÜDD"
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Country 'ÜDD' must be ISO conform!")
    }
}