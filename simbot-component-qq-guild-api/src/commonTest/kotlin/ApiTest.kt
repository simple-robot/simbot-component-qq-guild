package test

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ApiTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun apiReqTest() = runTest {
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
        val body: Map<String, String> = client.get("https://api.github.com/").body()
        println("body: $body")
    }

}

