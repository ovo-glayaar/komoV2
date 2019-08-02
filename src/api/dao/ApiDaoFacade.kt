package api.dao

import api.model.ApiEntity
import api.model.ApiResponseEntity
import api.model.ApisTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Closeable

interface ApiDaoFacade : Closeable {
    fun init()
    fun createApi(name: String, url: String, codeResponses: List<String>)
    fun updateApi(id: Int, name: String, url: String, codeResponses: List<String>)
    fun deleteApi(id: Int)
    fun getApi(id: Int): ApiEntity?
    fun getApiResponse(id: Int): ApiResponseEntity?
    fun getAllApis(): List<ApiEntity>
    fun getAllApiResponses(): List<ApiResponseEntity>
}

class ApiDaoFacadeImpl(private val database: Database) : ApiDaoFacade {

    override fun init() = transaction(database) {
        SchemaUtils.create(ApisTable)
    }

    override fun createApi(name: String, url: String, codeResponses: List<String>) = transaction(database) {
        val newApi = ApiEntity.new {
            this.name = name
            this.url = url
        }

        codeResponses.forEach {
            val responseComp = it.split("@#$")
            ApiResponseEntity.new {
                api = newApi
                code = responseComp[0]
                type = responseComp[1]
                response = responseComp[2]
            }
        }
        Unit
    }

    override fun updateApi(id: Int, name: String, url: String, codeResponses: List<String>) = transaction(database) {
        ApiEntity.findById(id)?.apply {
            this.name = name
            this.url = url
        }
        Unit
    }

    override fun deleteApi(id: Int) = transaction(database) {
        ApisTable.deleteWhere { ApisTable.id eq id }
        Unit
    }

    override fun getApi(id: Int): ApiEntity? = transaction(database) {
        ApiEntity.findById(id)
    }

    override fun getApiResponse(id: Int): ApiResponseEntity? = transaction(database) {
        ApiResponseEntity.findById(id)
    }

    override fun getAllApis(): List<ApiEntity> = transaction(database) {
        ApiEntity.all().toList()
    }

    override fun getAllApiResponses(): List<ApiResponseEntity> = transaction(database) {
        ApiResponseEntity.all().toList()
    }

    override fun close() {}

}