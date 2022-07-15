package com.poisonedyouth

import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class CustomerRepository {

    fun createNewCustomer(customer: Customer) = transaction {
        val addressEntityNew = if (customer.address.id == null) {
            AddressEntity.new{
                street = customer.address.street
                number = customer.address.number
                zipCode = customer.address.zipCode
                city = customer.address.city
                country = customer.address.country
            }
        } else {
            AddressEntity[customer.address.id!!.toLong()]
        }

        val customerEntityNew = CustomerEntity.new {
            firstName = customer.firstName
            lastName = customer.lastName
            birthDate = customer.birthDate
            email = customer.email
            addressEntity = addressEntityNew
        }
        customer.accounts.forEach {
            AccountEntity.new {
                number = it.number
                balance = it.balance
                customerEntity = customerEntityNew
            }
        }
        customerEntityNew.id.value
    }

    fun existsCustomerByEmail(email: String) = CustomerEntity.existsCustomerByEmail(email)

    fun getCustomerById(id: Long) = transaction {
        CustomerEntity.findById(id)?.let { customerEntity ->
            Customer(
                id = customerEntity.id.value,
                firstName = customerEntity.firstName,
                lastName = customerEntity.lastName,
                birthDate = customerEntity.birthDate,
                email = customerEntity.email,
                address = customerEntity.addressEntity.let {
                    Address(
                        id = it.id.value,
                        street = it.street,
                        number = it.number,
                        zipCode = it.zipCode,
                        city = it.city,
                        country = it.country
                    )
                },
                accounts = customerEntity.accounts.map {
                    Account(
                        id = it.id.value,
                        number = it.number,
                        balance = it.balance
                    )
                }.toSet()
            )
        }
    }
}

data class Customer(
    var id: Long? = null,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val email: String,
    val address: Address,
    val accounts: Set<Account>
)

data class Account(
    var id: Long? = null,
    val number: Int,
    val balance: Int,
)