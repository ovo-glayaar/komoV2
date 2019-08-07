package user.model

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table
import api.model.ApiResponsesTable

object UsersTable : IntIdTable() {
    val username = varchar("username", 50)
    val password = varchar("password", 50)
    val name = varchar("name", 50)
    val phone = varchar("phone", 15)
}

object UserApiStatesTable : Table() {
    val user = reference("userid", UsersTable).primaryKey(0)
    val apiresponse = reference("apiresponseid", ApiResponsesTable).primaryKey(1)
}

object UserTokensTable : IntIdTable() {
    val user = reference("userid", UsersTable)
    val token = varchar("token", 250)
}