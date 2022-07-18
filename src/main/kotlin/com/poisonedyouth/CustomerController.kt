package com.poisonedyouth

import com.poisonedyouth.ApiResult.Failure
import com.poisonedyouth.ApiResult.Success
import com.poisonedyouth.CustomerController.ResponseDto.ErrorDto
import com.poisonedyouth.CustomerController.ResponseDto.SuccessDto
import com.poisonedyouth.ErrorCode.DUPLICATE_EMAIL
import com.poisonedyouth.ErrorCode.GENERAL_ERROR
import com.poisonedyouth.ErrorCode.INVALID_DATE
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class CustomerController(
    private val customerApplicationService: CustomerApplicationService
) {

    fun addNewCustomer(customerDto: CustomerDto): Pair<HttpStatusCode, ResponseDto> {
        return customerApplicationService.addNewCustomer(customerDto).let { result ->
            when (result) {
                is Success -> handleSuccess(result)
                is Failure -> handleFailure(result)
            }

        }
    }

    private fun handleSuccess(result: Success<Long>): Pair<HttpStatusCode, SuccessDto> {
        return HttpStatusCode.Created to SuccessDto(result.value)
    }

    private fun handleFailure(
        result: Failure
    ): Pair<HttpStatusCode, ErrorDto> {

        val code = result.errorCode
        val status = when (code) {
            INVALID_DATE,
            DUPLICATE_EMAIL -> HttpStatusCode.BadRequest
            GENERAL_ERROR -> HttpStatusCode.InternalServerError
        }
        return status to ErrorDto(code.name, result.errorMessage)
    }

    sealed class ResponseDto {

        @Serializable
        data class ErrorDto(val errorCode: String, val errorMessage: String) : ResponseDto()

        @Serializable
        data class SuccessDto(val customerId: Long) : ResponseDto()
    }
}

