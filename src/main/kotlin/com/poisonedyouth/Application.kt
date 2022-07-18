package com.poisonedyouth

import com.poisonedyouth.plugins.configureRouting
import com.poisonedyouth.plugins.installContentNegotiation
import com.poisonedyouth.plugins.installKoin
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    DatabaseFactory.init()

    installKoin()
    installContentNegotiation()
    configureRouting()
}
