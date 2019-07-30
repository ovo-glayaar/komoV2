package user.model

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table
import api.model.ApiResponseTable

object UserTable : IntIdTable() {
    val username = varchar("username", 50)
    val password = varchar("password", 50)
    val name = varchar("name", 50)
}

object UserApiStatesTable : Table() {
    val user = reference("userid", UserTable).primaryKey(0)
    val apiresponse = reference("apiresponseid", ApiResponseTable).primaryKey(1)
}

object UserTokensTable : IntIdTable() {
    val user = reference("userid", UserTable)
    val token = varchar("token", 250)
}