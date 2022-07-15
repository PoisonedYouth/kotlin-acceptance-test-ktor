package com.poisonedyouth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.time.LocalDate

@ExtendWith(CleanDatabaseExtension::class)
class CustomerRepositoryTest : KoinTest {

    private val customerRepository by inject<CustomerRepository>()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { CustomerRepository() }
            })
    }

    @Test
    fun `createNewAddress persists new address`() {
        // given
        val address = Address(
            id = 0,
            street = "Main Street",
            number = "13A",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )
        val account1 = Account(
            id = 0,
            number = 12345,
            balance = 200
        )
        val account2 = Account(
            id = 0,
            number = 12346,
            balance = -150
        )
        val customer = Customer(
            id = 0,
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate.of(2001, 5, 10),
            email = "john.doe@mail.com",
            address = address,
            accounts = setOf(account1, account2)
        )

        // when
        val id = customerRepository.createNewCustomer(customer)

        // then
        assertThat(customerRepository.getCustomerById(id)).isNotNull
    }
}