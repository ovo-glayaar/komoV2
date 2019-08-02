package api.model

import org.jetbrains.exposed.dao.IntIdTable

object ApisTable: IntIdTable(){
    val name = varchar("name", 50)
    val url = varchar("url", 500)
}

object ApiResponsesTable: IntIdTable() {

    val api = reference("apiid", ApisTable)

    val code = varchar("code", 5)
    val type = varchar("type", 25)
    val response = varchar("response", 250000)

}