package com.poisonedyouth

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.DatabaseConfig.Companion
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CustomerTest {

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
            SchemaUtils.drop(CustomerTable, AccountTable, AddressTable)
            SchemaUtils.create(AccountTable, CustomerTable, AddressTable)
        }
    }

    @Test
    fun `save Customer is possible`() {
        // given
        val addressNew = Address.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }


        // when
        val customerNew = Customer.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            address = addressNew
        }
        Account.new {
            number = 12345
            balance = 200
            customer = customerNew
        }
        Account.new {
            number = 12346
            balance = -150
            customer = customerNew
        }

        // then
        assertThat(Customer.findById(customerNew.id)).isEqualTo(customerNew)
    }

    @Test
    fun `delete Customer is possible`() {
        // given
        transaction {
            val addressNew =
                Address.new {
                    street = "Main Street"
                    number = "13"
                    zipCode = 90001
                    city = "Los Angeles"
                    country = "US"
                }

            val customerNew = Customer.new {
                firstName = "John"
                lastName = "Doe"
                birthDate = LocalDate.of(2001, 5, 10)
                email = "john.doe@mail.com"
                address = addressNew
            }

            val account = Account.new {
                number = 12345
                balance = 200
                customer = customerNew
            }
            Account.new {
                number = 12346
                balance = -150
                customer = customerNew
            }

            // when
            Customer.deleteCustomer(customerNew)

            // then
            assertThat(Customer.findById(customerNew.id)).isNull()
            assertThat(Account.findById(account.id)).isNull()
            assertThat(Address.all()).isNotEmpty
        }
    }

    @Test
    fun `save Customer not allows duplicate email`() {
        // given
        val addressNew = Address.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        val customerNew = Customer.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            address = addressNew
        }
        Account.new {
            number = 12345
            balance = 200
            customer = customerNew
        }
        Account.new {
            number = 12346
            balance = -150
            customer = customerNew
        }

        // when + then
        Assertions.assertThatThrownBy {
            Customer.new {
                firstName = "Duplicate"
                lastName = "Customer"
                birthDate = LocalDate.of(1984, 12, 1)
                email = "john.doe@mail.com"
                address = addressNew
            }

        }.isInstanceOf(ExposedSQLException::class.java)
    }
//
//    @Test
//    fun `save Customer without saved address fails`() {
//        // given
//        val address = Address(
//            street = "Main Street",
//            number = "13",
//            zipCode = 90001,
//            city = "Los Angeles",
//            country = "US"
//        )
//
//        val customer = Customer(
//            firstName = "John",
//            lastName = "Doe",
//            birthdate = LocalDate.of(2001, 5, 10),
//            email = "john.doe@mail.com",
//            address = address,
//            accounts = setOf(
//                Account(
//                    number = 12345,
//                    balance = 200
//                ),
//                Account(
//                    number = 12346,
//                    balance = -150
//                )
//            )
//        )
//
//        // when + then
//        Assertions.assertThatThrownBy {
//            customerRepository.save(customer)
//        }.isInstanceOf(InvalidDataAccessApiUsageException::class.java)
//    }
//
//    @Test
//    fun `findByFirstNameAndLastName returns matching customer`() {
//        // given
//        val address = Address(
//            street = "Main Street",
//            number = "13",
//            zipCode = 90001,
//            city = "Los Angeles",
//            country = "US"
//        )
//        addressRepository.save(address)
//
//        val customer = Customer(
//            firstName = "John",
//            lastName = "Doe",
//            birthdate = LocalDate.of(2001, 5, 10),
//            email = "john.doe@mail.com",
//            address = address,
//            accounts = setOf(
//                Account(
//                    number = 12345,
//                    balance = 200
//                ),
//                Account(
//                    number = 12346,
//                    balance = -150
//                )
//            )
//        )
//        customerRepository.save(customer)
//
//        // when
//        val actual = customerRepository.findByFirstNameAndLastName("John", "Doe")
//
//        // then
//        assertThat(actual.get()).isEqualTo(customer)
//    }
//
//    @Test
//    fun `existsCustomerByEmail returns true if customer exists`() {
//        // given
//        val address = Address(
//            street = "Main Street",
//            number = "13",
//            zipCode = 90001,
//            city = "Los Angeles",
//            country = "US"
//        )
//        addressRepository.save(address)
//
//        val customer = Customer(
//            firstName = "John",
//            lastName = "Doe",
//            birthdate = LocalDate.of(2001, 5, 10),
//            email = "john.doe@mail.com",
//            address = address,
//            accounts = setOf(
//                Account(
//                    number = 12345,
//                    balance = 200
//                ),
//                Account(
//                    number = 12346,
//                    balance = -150
//                )
//            )
//        )
//        customerRepository.save(customer)
//
//        // when
//        val actual = customerRepository.existsCustomerByEmail(customer.email)
//
//        // then
//        assertThat(actual).isTrue
//    }
//
//    @Test
//    fun `existsCustomerByEmail returns false if customer not exists`() {
//        // given
//        val address = Address(
//            street = "Main Street",
//            number = "13",
//            zipCode = 90001,
//            city = "Los Angeles",
//            country = "US"
//        )
//        addressRepository.save(address)
//
//        val customer = Customer(
//            firstName = "John",
//            lastName = "Doe",
//            birthdate = LocalDate.of(2001, 5, 10),
//            email = "john.doe@mail.com",
//            address = address,
//            accounts = setOf(
//                Account(
//                    number = 12345,
//                    balance = 200
//                ),
//                Account(
//                    number = 12346,
//                    balance = -150
//                )
//            )
//        )
//        customerRepository.save(customer)
//
//        // when
//        val actual = customerRepository.existsCustomerByEmail("otherCustomer@mail.com")
//
//        // then
//        assertThat(actual).isFalse
//    }
}