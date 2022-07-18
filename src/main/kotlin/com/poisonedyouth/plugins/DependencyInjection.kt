package com.poisonedyouth.plugins

import com.poisonedyouth.AddressRepository
import com.poisonedyouth.CustomerApplicationService
import com.poisonedyouth.CustomerController
import com.poisonedyouth.CustomerRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val dependencyInjection = module {
    single { AddressRepository() }
    single { CustomerRepository() }
    single { CustomerApplicationService(get(), get()) }
    single { CustomerController(get())}
}

fun Application.installKoin() {
    install(Koin) {
        modules(dependencyInjection)
    }
}