package com.poisonedyouth

import com.poisonedyouth.ApiResult.Failure
import com.poisonedyouth.ApiResult.Success
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import java.time.LocalDate
import org.koin.test.inject

@ExtendWith(CleanDatabaseExtension::class)
class CustomerApplicationServiceTest : KoinTest {

    private val addressRepository by inject<AddressRepository>()
    private val customerRepository by inject<CustomerRepository>()

    private val customerApplicationService  by inject<CustomerApplicationService>()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { AddressRepository() }
                single { CustomerRepository() }
                single { CustomerApplicationService(get(), get()) }
            })
    }


    @Test
    fun `addNewCustomer returns failure result for duplicate email`() {
        // given
        val email = "john.doe@mail.com"
        val customer = createCustomer(email)

        val addressDto = AddressDto(
            street = customer.address.street,
            number = customer.address.number,
            zipCode = customer.address.zipCode,
            city = customer.address.city,
            country = customer.address.country
        )

        val customerDuplicate = CustomerDto(
            firstName = "John",
            lastName = "Doe",
            birthdate = "10.05.2000",
            email = "john.doe@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val actual = customerApplicationService.addNewCustomer(customerDuplicate)

        // then
        assertThat(actual).isInstanceOf(Failure::class.java)
        assertThat((actual as Failure).errorCode).isEqualTo(ErrorCode.DUPLICATE_EMAIL)
        assertThat(actual.errorMessage).isEqualTo("For the email 'john.doe@mail.com' a customer already exists!")
    }

    @Test
    fun `addNewCustomer returns failure result for invalid birthdate`() {
        // given
        val email = "john.doe@mail.com"
        val customer = createCustomer(email)

        val addressDto = AddressDto(
            street = customer.address.street,
            number = customer.address.number,
            zipCode = customer.address.zipCode,
            city = customer.address.city,
            country = customer.address.country
        )

        val customerDuplicate = CustomerDto(
            firstName = "John",
            lastName = "Doe",
            birthdate = "2000.5.10",
            email = "john.doe@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val actual = customerApplicationService.addNewCustomer(customerDuplicate)

        // then
        assertThat(actual).isInstanceOf(Failure::class.java)
        assertThat((actual as Failure).errorCode).isEqualTo(ErrorCode.INVALID_DATE)
        assertThat(actual.errorMessage).isEqualTo("The birthdate '2000.5.10' is not in expected format (dd.MM.yyyy)!")
    }

    @Test
    fun `addNewCustomer returns success result for unique email`() {
        // given
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )
        addressRepository.createNewAddress(address)
        val addressDto = AddressDto(
            street = address.street,
            number = address.number,
            zipCode = address.zipCode,
            city = address.city,
            country = address.country
        )

        val customer = CustomerDto(
            firstName = "John",
            lastName = "Doe",
            birthdate = "10.05.2000",
            email = "john.doe@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        val actual = customerApplicationService.addNewCustomer(customer)

        // then
        assertThat(actual).isInstanceOf(Success::class.java)
        assertThat((actual as Success).value).isNotNull
    }

    @Test
    fun `addNewCustomer not persists address twice`() {
        // given
        val email = "john.doe@mail.com"
        createCustomer(email)

        val addressDto = AddressDto(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )

        val customer = CustomerDto(
            firstName = "Max",
            lastName = "DeMarco",
            birthdate = "10.05.2000",
            email = "max.demarco@mail.com",
            address = addressDto,
            accounts = setOf(
                AccountDto(
                    number = 12345,
                    balance = 200
                ),
                AccountDto(
                    number = 12346,
                    balance = -150
                )
            )
        )

        // when
        customerApplicationService.addNewCustomer(customer)

        // then
        assertThat(addressRepository.findAll()).hasSize(1)
    }


    private fun createCustomer(email: String): Customer {
        val address = Address(
            street = "Main Street",
            number = "13",
            zipCode = 90001,
            city = "Los Angeles",
            country = "US"
        )
        val customer = Customer(
            firstName = "John",
            lastName = "Doe",
            birthDate = LocalDate.of(2000, 5, 10),
            email = email,
            address = address,
            accounts = setOf(
                Account(
                    id = 0,
                    number = 12345,
                    balance = 200
                ),
                Account(
                    id = 0,
                    number = 12346,
                    balance = -150
                )
            )
        )
        customerRepository.createNewCustomer(customer)
        return customer
    }
}