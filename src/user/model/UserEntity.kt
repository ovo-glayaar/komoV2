package user.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import api.model.ApiResponseEntity

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)

    var username by UsersTable.username
    var password by UsersTable.password
    var name by UsersTable.name
    var phone by UsersTable.phone
    //val userToken: List<UserToken>,
    //val userApiState: List<UserApiState>

    var apiStates by ApiResponseEntity via UserApiStatesTable
}