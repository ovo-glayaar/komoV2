package api.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class ApiResponseEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ApiResponseEntity>(ApiResponseTable)

    var api by ApiEntity referencedOn ApiResponseTable.api

    var code by ApiResponseTable.code
    var type by ApiResponseTable.type
    var response by ApiResponseTable.response
}