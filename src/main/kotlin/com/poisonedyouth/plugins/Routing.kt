package com.poisonedyouth.plugins

import com.poisonedyouth.ApiResult
import com.poisonedyouth.ApiResult.Failure
import com.poisonedyouth.ApiResult.Success
import com.poisonedyouth.CustomerApplicationService
import com.poisonedyouth.CustomerController
import com.poisonedyouth.CustomerDto
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.handleFailure
import io.ktor.server.response.*
import io.ktor.server.request.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val service by inject<CustomerController>()

    routing {
        post("/api/v1/customer") {
            val customer = call.receive<CustomerDto>()
            val result = service.addNewCustomer(customer)
            call.respond(result.first, result.second)
        }
    }
}
