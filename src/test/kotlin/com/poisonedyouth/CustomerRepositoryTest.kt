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
            street = "Main Street",
            number = "13A",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )
        val account1 = Account(
            number = 12345,
            balance = 200
        )
        val account2 = Account(
            number = 12346,
            balance = -150
        )
        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate.of(2001, 5, 10),
            email = "john.doe@mail.com",
            address = address,
            accounts = setOf(account1, account2)
        )

        // when
        customerRepository.createNewCustomer(customer)

        // then
        assertThat(customerRepository.getCustomerById(1L)).isNotNull
    }
}