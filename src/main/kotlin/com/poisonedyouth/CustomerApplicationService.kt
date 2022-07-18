package com.poisonedyouth

import com.poisonedyouth.ApiResult.Failure
import com.poisonedyouth.ApiResult.Success
import com.poisonedyouth.ErrorCode.DUPLICATE_EMAIL
import com.poisonedyouth.ErrorCode.GENERAL_ERROR
import com.poisonedyouth.ErrorCode.INVALID_DATE
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CustomerApplicationService(
    private val customerRepository: CustomerRepository,
    private val addressRepository: AddressRepository
) {


    fun addNewCustomer(customerDto: CustomerDto): ApiResult<Long> {
        try {
            val customer = mapCustomerDtoToCustomer(customerDto)

            if (customerRepository.existsCustomerByEmail(customer.email)) {
                return Failure(DUPLICATE_EMAIL, "For the email '${customer.email}' a customer already exists!")
            }

            val address = addressRepository.findAddressByZipCode(customer.address.zipCode)
            val customerToPersist = if (address == null) {
                customer
            } else {
                customer.copy(address = address)
            }

            return Success(customerRepository.createNewCustomer(customerToPersist))

        } catch (e: DateTimeParseException) {
            return Failure(
                INVALID_DATE,
                "The birthdate '${customerDto.birthdate}' is not in expected format (dd.MM.yyyy)!"
            )
        } catch (e: Exception) {
            return Failure(GENERAL_ERROR, "An unexpected error occurred (${e.message}!")
        }
    }

    private fun mapCustomerDtoToCustomer(customerDto: CustomerDto): Customer {
        return Customer(
            firstName = customerDto.firstName,
            lastName = customerDto.lastName,
            birthDate = LocalDate.parse(customerDto.birthdate, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            email = customerDto.email,
            address = Address(
                id = null,
                street = customerDto.address.street,
                number = customerDto.address.number,
                zipCode = customerDto.address.zipCode,
                city = customerDto.address.city,
                country = customerDto.address.country
            ),
            accounts = customerDto.accounts.map {
                Account(
                    number = it.number,
                    balance = it.balance
                )
            }.toSet()
        )
    }
}

sealed class ApiResult<out T> {
    internal data class Failure(val errorCode: ErrorCode, val errorMessage: String) : ApiResult<Nothing>()
    internal data class Success<T>(val value: T) : ApiResult<T>()
}

enum class ErrorCode {
    DUPLICATE_EMAIL,
    INVALID_DATE,
    GENERAL_ERROR,
}
