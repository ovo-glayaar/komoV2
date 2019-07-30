package api.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class ApiEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ApiEntity>(ApiTable)

    var name by ApiTable.name
    var url by ApiTable.url
}