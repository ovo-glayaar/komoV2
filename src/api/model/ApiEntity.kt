package api.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

class ApiEntity(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<ApiEntity>(ApisTable)

    var name by ApisTable.name
    var url by ApisTable.url
}