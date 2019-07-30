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
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.sql.Database
import org.ovo.mockserver.api.router.setApiRoute
import org.ovo.mockserver.user.dao.UserDaoFacade
import org.ovo.mockserver.user.dao.UserDaoFacadeImpl
import org.ovo.mockserver.user.router.setUserRoute
import org.slf4j.event.Level

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

    routing {
        static("/users/static") {
            resources("static")
        }

        route("/index") {
            get {
                val params = mapOf("title" to "Main Page")
                call.respond(FreeMarkerContent("indexV2.ftl", params, ""))
            }
        }

        route("{path...}") {
            get("/") {
                val action = call.parameters.getAll("path")?.joinToString(separator = "/")
                if (action != null) {

                    //val token = call.request.headers.get("Authorization").orEmpty()
                    val token = "user_a||123456||02"
                    val sessionId = call.request.headers.get("cs-session-id").orEmpty()


                    val response = userDao.getResponseByUser(token, action).orEmpty()
                    //TODO: implement logic
                    call.respond(TextContent(response, ContentType.Application.Json))
                }
            }
        }
    }

    setApiRoute(apiDao)

    setUserRoute(userDao, apiDao)
}

data class IndexData(val items: List<Int>)