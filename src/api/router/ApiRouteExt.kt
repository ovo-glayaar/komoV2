package org.ovo.mockserver.api.router

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

fun Application.setApiRoute(apiDao: ApiDaoFacade) {
    routing {
        route("/apis/edit") {
            get {
                val id = call.request.queryParameters["id"]
                if (id != null) {
                    call.respond(FreeMarkerContent("api_edit.ftl",
                            mapOf("api" to apiDao.getApi(id.toInt()))))
                }
            }
            post {
                val postParameters: Parameters = call.receiveParameters()
                val id = postParameters["id"]
                if (id != null) {
                    apiDao.updateApi(id.toInt(),
                            postParameters["name"].orEmpty(),
                            postParameters["url"].orEmpty(),
                            postParameters.getAll("apiResponse") ?: listOf())
                }
                call.respond(FreeMarkerContent("apis.ftl", mapOf("apis" to apiDao.getAllApis())))
            }
        }
        route("/apis/new") {
            get {
                call.respond(FreeMarkerContent("api_edit.ftl", null))
            }
            post {
                val postParameters: Parameters = call.receiveParameters()
                apiDao.createApi(postParameters["name"].orEmpty(),
                        postParameters["url"].orEmpty(),
                        postParameters.getAll("apiResponse") ?: listOf())

                call.respond(FreeMarkerContent("apis.ftl", mapOf("apis" to apiDao.getAllApis())))
            }
        }
        route("apis/delete") {
            get {
                val id = call.request.queryParameters["id"]
                if (id != null) {
                    apiDao.deleteApi(id.toInt())
                    call.respond(FreeMarkerContent("apis.ftl", mapOf("apis" to apiDao.getAllApis())))
                }
            }
        }
        route("apis") {
            get {
                call.respond(FreeMarkerContent("apis.ftl", mapOf("apis" to apiDao.getAllApis())))
            }
        }
    }
}