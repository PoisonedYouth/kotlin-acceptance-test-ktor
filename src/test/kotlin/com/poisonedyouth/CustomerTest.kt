package com.poisonedyouth

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

class CustomerTest {

    @Test
    fun `createCustomerId returns 5 digits id`(){
        // given
        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate.of(2000, 1, 2),
            email = "john.doe@mail.com",
            address = Address(
                street = "Main Street",
                number = "13",
                zipCode = 90001,
                city = "Los Angeles",
                country = "US",
            ),
            accounts = emptySet()
        )

        // when
        val actual = customer.customerId

        // then
        assertThat(actual).isBetween(10000, 99999)
    }

    @Test
    fun `createCustomerId is random`(){
        // given
        val customer1 = Customer(
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate.of(2000, 1, 2),
            email = "john.doe@mail.com",
            address = Address(
                street = "Main Street",
                number = "13",
                zipCode = 90001,
                city = "Los Angeles",
                country = "US",
            ),
            accounts = emptySet()
        )

        val customer2 = Customer(
            firstName = "Max",
            lastName = "DeMarco",
            birthDate = LocalDate.of(1987, 1, 2),
            email = "max.demarco@mail.com",
            address = Address(
                street = "Main Street",
                number = "13",
                zipCode = 90001,
                city = "Los Angeles",
                country = "US",
            ),
            accounts = emptySet()
        )

        // when
        val actual1 = customer1.customerId
        val actual2 = customer2.customerId

        // then
        assertThat(actual1).isNotEqualTo(actual2)
    }

    @Test
    fun `create Customer fails if firstName contains invalid characters`(){
        // given - when - then
        assertThatThrownBy {
            Customer(
                firstName = "J%hn",
                lastName = "Doe",
                birthDate = LocalDate.of(2000, 1, 2),
                email = "john.doe@mail.com",
                address = Address(
                    street = "Main Street",
                    number = "13",
                    zipCode = 90001,
                    city = "Los Angeles",
                    country = "US",
                ),
                accounts = emptySet()
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Firstname 'J%hn' contains special characters!")
    }

    @Test
    fun `create Customer fails if lastName contains invalid characters`(){
        // given - when - then
        assertThatThrownBy {
            Customer(
                firstName = "John",
                lastName = "D/e",
                birthDate = LocalDate.of(2000, 1, 2),
                email = "john.doe@mail.com",
                address = Address(
                    street = "Main Street",
                    number = "13",
                    zipCode = 90001,
                    city = "Los Angeles",
                    country = "US",
                ),
                accounts = emptySet()
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Lastname 'D/e' contains special characters!")
    }

    @Test
    fun `create Customer fails if age is below 18 years`(){
        // given - when - then
        assertThatThrownBy {
            Customer(
                firstName = "John",
                lastName = "Doe",
                birthDate = LocalDate.of(2019, 1, 2),
                email = "john.doe@mail.com",
                address = Address(
                    street = "Main Street",
                    number = "13",
                    zipCode = 90001,
                    city = "Los Angeles",
                    country = "US",
                ),
                accounts = emptySet()
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Age must be between 18 and 100!")
    }

    @Test
    fun `create Customer fails if age is above 100 years`(){
        // given - when - then
        assertThatThrownBy {
            Customer(
                firstName = "John",
                lastName = "Doe",
                birthDate = LocalDate.of(1920, 1, 2),
                email = "john.doe@mail.com",
                address = Address(
                    street = "Main Street",
                    number = "13",
                    zipCode = 90001,
                    city = "Los Angeles",
                    country = "US",
                ),
                accounts = emptySet()
            )
        }.isInstanceOf(IllegalArgumentException::class.java).hasMessage("Age must be between 18 and 100!")
    }
}