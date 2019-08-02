package api.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class ApiResponseEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ApiResponseEntity>(ApiResponsesTable)

    var api by ApiEntity referencedOn ApiResponsesTable.api

    var code by ApiResponsesTable.code
    var type by ApiResponsesTable.type
    var response by ApiResponsesTable.response
}