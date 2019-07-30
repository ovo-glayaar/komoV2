package user.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import api.model.ApiResponseEntity

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var password by UserTable.password
    var name by UserTable.name
    //val userToken: List<UserToken>,
    //val userApiState: List<UserApiState>

    var apiStates by ApiResponseEntity via UserApiStatesTable
}