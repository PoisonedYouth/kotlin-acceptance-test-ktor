package com.poisonedyouth

import org.assertj.core.api.Assertions.assertThat
import com.poisonedyouth.plugins.configureRouting
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.Test
import java.io.File

class CustomerEntityAcceptanceTest {

    @Test
    fun `scenario save customer is successful`() = testApplication {
        // given
        application {
            configureRouting()
        }
        // when
        client.post("/api/v1/customer"){
            setBody(File("src/test/resources/payload.json").readText())
            contentType(ContentType.Application.Json)
        }.apply {
            assertThat(status).isEqualTo(HttpStatusCode.Created)
            assertThat(bodyAsText()).matches("\\{\"customerId\":\\d{5}}")
        }
    }

}