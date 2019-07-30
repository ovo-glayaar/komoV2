package org.ovo.mockserver.user.router

import api.dao.ApiDaoFacade
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import org.ovo.mockserver.user.dao.UserDaoFacade

fun Application.setUserRoute(userDao: UserDaoFacade, apiDao: ApiDaoFacade) {
    routing {
        route("/users/login") {
            get {
                call.respond(FreeMarkerContent("login.ftl", kotlinx.html.emptyMap))
            }
        }

        route("/users/edit") {
            get {
                val id = call.request.queryParameters["id"]
                if (id != null) {
                    call.respond(FreeMarkerContent("user_edit.ftl",
                            mapOf("user" to userDao.getUser(id.toInt()), "apis" to apiDao.getAllApis())))
                }
            }
            post {
                val postParameters: Parameters = call.receiveParameters()
                val id = postParameters["id"]
                if (id != null) {

                    userDao.updateUser(id.toInt(),
                            postParameters["username"].orEmpty(),
                            postParameters["password"].orEmpty(),
                            postParameters["name"].orEmpty(),
                            "",
                            postParameters.getAll("ApiState")?.map { it.toInt() }?.toList().orEmpty())

                }
                call.respond(FreeMarkerContent("users.ftl", mapOf("users" to userDao.getAllUsers())))
            }
        }

        route("/users/new") {
            get {
                call.respond(FreeMarkerContent("user_edit.ftl",
                        mapOf("apis" to apiDao.getAllApis())))
            }
            post {
                val postParameters: Parameters = call.receiveParameters()

                userDao.createUser(postParameters["username"].orEmpty(),
                        postParameters["password"].orEmpty(),
                        postParameters["name"].orEmpty(),
                        postParameters.getAll("ApiState")?.map { it.toInt() }?.toList() ?: listOf())

                call.respond(FreeMarkerContent("users.ftl", mapOf("users" to userDao.getAllUsers())))
            }
        }
        route("users/delete") {
            get {
                val id = call.request.queryParameters["id"]
                if (id != null) {
                    userDao.deleteUser(id.toInt())
                    call.respond(FreeMarkerContent("users.ftl", mapOf("users" to userDao.getAllUsers())))
                }
            }
        }
        route("users") {
            get {
                call.respond(FreeMarkerContent("users.ftl", mapOf("users" to userDao.getAllUsers())))
            }
        }
    }
}