package com.poisonedyouth

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(CleanDatabaseExtension::class)
internal class CustomerEntityTest {

    @Test
    fun `save Customer is possible`() {
        // given
        val addressEntityNew = AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }


        // when
        val customerEntityNew = CustomerEntity.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            addressEntity = addressEntityNew
        }
        AccountEntity.new {
            number = 12345
            balance = 200
            customerEntity = customerEntityNew
        }
        AccountEntity.new {
            number = 12346
            balance = -150
            customerEntity = customerEntityNew
        }

        // then
        assertThat(CustomerEntity.findById(customerEntityNew.id)).isEqualTo(customerEntityNew)
    }

    @Test
    fun `delete Customer is possible`() {
        // given
        transaction {
            val addressEntityNew =
                AddressEntity.new {
                    street = "Main Street"
                    number = "13"
                    zipCode = 90001
                    city = "Los Angeles"
                    country = "US"
                }

            val customerEntityNew = CustomerEntity.new {
                firstName = "John"
                lastName = "Doe"
                birthDate = LocalDate.of(2001, 5, 10)
                email = "john.doe@mail.com"
                addressEntity = addressEntityNew
            }

            val accountEntity = AccountEntity.new {
                number = 12345
                balance = 200
                customerEntity = customerEntityNew
            }
            AccountEntity.new {
                number = 12346
                balance = -150
                customerEntity = customerEntityNew
            }

            // when
            CustomerEntity.deleteCustomer(customerEntityNew)

            // then
            assertThat(CustomerEntity.findById(customerEntityNew.id)).isNull()
            assertThat(AccountEntity.findById(accountEntity.id)).isNull()
            assertThat(AddressEntity.all()).isNotEmpty
        }
    }

    @Test
    fun `save Customer not allows duplicate email`() {
        // given
        val addressEntityNew = AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        val customerEntityNew = CustomerEntity.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            addressEntity = addressEntityNew
        }
        AccountEntity.new {
            number = 12345
            balance = 200
            customerEntity = customerEntityNew
        }
        AccountEntity.new {
            number = 12346
            balance = -150
            customerEntity = customerEntityNew
        }

        // when + then
        Assertions.assertThatThrownBy {
            CustomerEntity.new {
                firstName = "Duplicate"
                lastName = "Customer"
                birthDate = LocalDate.of(1984, 12, 1)
                email = "john.doe@mail.com"
            }

        }.isInstanceOf(ExposedSQLException::class.java)
    }

    @Test
    fun `save Customer without saved address fails`() {
        // given
        val addressEntityNotPersisted = AddressEntity(EntityID(1L, AddressTable))

        // when + then
        Assertions.assertThatThrownBy {
            CustomerEntity.new {
                firstName = "John"
                lastName = "Doe"
                birthDate = LocalDate.of(2001, 5, 10)
                email = "john.doe@mail.com"
                addressEntity = addressEntityNotPersisted
            }
        }.isInstanceOf(IllegalStateException::class.java)

    }

    @Test
    fun `findByFirstNameAndLastName returns matching customer`() {
        // given
        val addressEntityNew = AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        val customerEntityNew = CustomerEntity.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            addressEntity = addressEntityNew
        }
        AccountEntity.new {
            number = 12345
            balance = 200
            customerEntity = customerEntityNew
        }
        AccountEntity.new {
            number = 12346
            balance = -150
            customerEntity = customerEntityNew
        }


        // when
        val actual = CustomerEntity.findByFirstNameAndLastName("John", "Doe")

        // then
        assertThat(actual).isEqualTo(customerEntityNew)
    }

    @Test
    fun `existsCustomerByEmail returns true if customer exists`() {
        // given
        val addressEntityNew = AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        val customerEntityNew = CustomerEntity.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            addressEntity = addressEntityNew
        }
        AccountEntity.new {
            number = 12345
            balance = 200
            customerEntity = customerEntityNew
        }
        AccountEntity.new {
            number = 12346
            balance = -150
            customerEntity = customerEntityNew
        }

        // when
        val actual = CustomerEntity.existsCustomerByEmail(customerEntityNew.email)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `existsCustomerByEmail returns false if customer not exists`() {
        // given
        val addressEntityNew = AddressEntity.new {
            street = "Main Street"
            number = "13"
            zipCode = 90001
            city = "Los Angeles"
            country = "US"
        }

        val customerEntityNew = CustomerEntity.new {
            firstName = "John"
            lastName = "Doe"
            birthDate = LocalDate.of(2001, 5, 10)
            email = "john.doe@mail.com"
            addressEntity = addressEntityNew
        }
        AccountEntity.new {
            number = 12345
            balance = 200
            customerEntity = customerEntityNew
        }
        AccountEntity.new {
            number = 12346
            balance = -150
            customerEntity = customerEntityNew
        }

        // when
        val actual = CustomerEntity.existsCustomerByEmail("otherCustomer@mail.com")

        // then
        assertThat(actual).isFalse
    }
}