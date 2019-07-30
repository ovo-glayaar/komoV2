package api.model

import org.jetbrains.exposed.dao.IntIdTable

object ApiTable: IntIdTable(){
    val name = varchar("name", 50)
    val url = varchar("url", 500)
}

object ApiResponseTable: IntIdTable() {

    val api = reference("apiid", ApiTable)

    val code = varchar("code", 5)
    val type = varchar("type", 25)
    val response = varchar("response", 250000)

}