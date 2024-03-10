package eu.javimar.wirelessval.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import eu.javimar.wirelessval.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://valencia.opendatasoft.com/"

interface INetworkModule {
    val networkClient: HttpClient
}

class NetworkModule(
    private val context: Context
): INetworkModule {
    @OptIn(ExperimentalSerializationApi::class)
    override val networkClient: HttpClient by lazy {
        HttpClient(provideEngine(context)) {
            expectSuccess = true
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.BODY
                filter { request ->
                    request.url.host.contains(BASE_URL)
                }
                sanitizeHeader { header ->
                    header == HttpHeaders.Authorization
                }
            }
            defaultRequest {
                // add base url for all request
                url(BASE_URL)
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }
    }

    private fun provideEngine(context: Context): HttpClientEngine {
        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
        // creating the Ktor HttpClienEngine
        return OkHttp.create {
            config {
                connectTimeout(BuildConfig.BASE_API_TIMEOUT, TimeUnit.SECONDS)
            }
            addInterceptor(chuckerInterceptor)
        }
    }
}