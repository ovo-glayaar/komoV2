package org.ovo.mockserver

import api.dao.ApiDaoFacade
import api.dao.ApiDaoFacadeImpl
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.content.TextContent
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.sql.Database
import org.ovo.mockserver.api.router.setApiRoute
import org.ovo.mockserver.user.dao.UserDaoFacade
import org.ovo.mockserver.user.dao.UserDaoFacadeImpl
import org.ovo.mockserver.user.router.setUserRoute
import org.slf4j.event.Level
import java.text.DateFormat
import java.util.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

private val database = Database.connect("jdbc:postgresql://john.db.elephantsql.com:5432/ehhtftpv",
        driver = "org.postgresql.Driver", user = "ehhtftpv", password = "Q5FO8evgZ7RV_Jz708vpdwakG7sWSnhF")

private val apiDao: ApiDaoFacade by lazy {
    ApiDaoFacadeImpl(database)
}

private val userDao: UserDaoFacade by lazy {
    UserDaoFacadeImpl(database, apiDao)
}

fun Application.module() {
    apiDao.init()
    userDao.init()

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            register(ContentType.Application.Json, GsonConverter())
        }
    }

    install(DoubleReceive)

    routing {
        static("/users/static") {
            resources("static")
        }
    }

    setApiRoute(apiDao)

    setUserRoute(userDao, apiDao)

    routing {
        route("/index") {
            get {
                val params = mapOf("title" to "Main Page")
                call.respond(FreeMarkerContent("indexV2.ftl", params, ""))
            }
        }

        route("{path...}") {
            delete("/") {
                val action = call.parameters.getAll("path")?.joinToString(separator = "/")
                var response = "{ \"status\": \"FAIL\" }"

                action?.takeIf { it.isNotBlank() }?.let {
                    val token = call.request.headers["Authorization"].orEmpty()
                    response = userDao.getResponseByUser(token, action, "DELETE").orEmpty()
                }

                call.respond(TextContent(response, ContentType.Application.Json))

            }
            put("/") {
                val action = call.parameters.getAll("path")?.joinToString(separator = "/")
                var response = "{ \"status\": \"FAIL\" }"

                action?.takeIf { it.isNotBlank() }?.let {
                    val token = call.request.headers["Authorization"].orEmpty()
                    response = userDao.getResponseByUser(token, action, "PUT").orEmpty()
                }

                call.respond(TextContent(response, ContentType.Application.Json))

            }
            get("/") {
                val action = call.parameters.getAll("path")?.joinToString(separator = "/")
                var response = "{ \"status\": \"FAIL\" }"

                action?.takeIf { it.isNotBlank() }?.let {
                    val token = call.request.headers["Authorization"].orEmpty()
                    response = userDao.getResponseByUser(token, action, "GET").orEmpty()
                }

                call.respond(TextContent(response, ContentType.Application.Json))

            }
            post("/") {
                val action = call.parameters.getAll("path")?.joinToString(separator = "/")
                var response = "{ \"status\": \"FAIL\" }"

                action?.takeIf { it.isNotBlank() }?.let {
                    when (action) {
                        "v2.0/api/auth/customer/login2FA" -> {
                            val postParameters = call.receive<CustomerLogin>()
                            val phone = postParameters.mobile

                            //Generate new token
                            val STRING_LENGTH = 10
                            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                            val token = (1..STRING_LENGTH)
                                .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
                                .map(charPool::get)
                                .joinToString("")

                            userDao.updateTokenByPhone(phone, token)

                            userDao.getResponseByUser(token, action, "POST")?.let {
                                response = it.replace("@token", token)
                            }
                        }
                        "v2.0/api/auth/customer/login2FA/verify" -> {
                            val postParameters = call.receive<Verify>()
                            val refId = postParameters.refId

                            userDao.getResponseByUser(refId, action, "POST")?.let {
                                response = it.replace("@token", refId)
                            }
                        }
                        "v2.0/api/auth/customer/loginSecurityCode/verify" -> {
                            val postParameters = call.receive<SecurityCode>()
                            val accessToken = postParameters.updateAccessToken.orEmpty()

                            userDao.getResponseByUser(accessToken, action, "POST")?.let {
                                val now = Calendar.getInstance().timeInMillis / 1000
                                val expired = Calendar.getInstance().apply {
                                    set(this.get(Calendar.YEAR) + 1, this.get(Calendar.MONTH), this.get(Calendar.DATE))
                                }.timeInMillis / 1000

                                response = it.replace("@token", accessToken)
                                    .replace("@time", now.toString())
                                    .replace("@expire", expired.toString())
                                    .replace("@accessToken", accessToken)
                            }

                        }
                        else -> {

                            val token = call.request.headers["Authorization"].orEmpty()

                            response = userDao.getResponseByUser(token, action, "POST").orEmpty()

                        }
                    }
                }

                call.respond(TextContent(response, ContentType.Application.Json))
            }
        }
    }
}

data class IndexData(val items: List<Int>)