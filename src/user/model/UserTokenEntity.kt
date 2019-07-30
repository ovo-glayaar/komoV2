package org.ovo.mockserver.user.model

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import user.model.UserTokensTable
import user.model.UserEntity

class UserTokenEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTokenEntity>(UserTokensTable)

    var user by UserEntity referencedOn UserTokensTable.user
    var token by UserTokensTable.token
}