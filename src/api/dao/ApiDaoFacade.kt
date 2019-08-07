package api.dao

import api.model.ApiEntity
import api.model.ApiResponseEntity
import api.model.ApiResponsesTable
import api.model.ApisTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.ovo.mockserver.api.model.ApiModel
import org.ovo.mockserver.api.model.ApiResponseModel
import java.io.Closeable

interface ApiDaoFacade : Closeable {
    fun init()
    fun createApi(name: String, url: String, codeResponses: List<String>)
    fun updateApi(id: Int, name: String, url: String, codeResponses: List<String>)
    fun deleteApi(id: Int)
    fun getApi(id: Int): ApiModel?
    fun getRawApiResponse(id: Int): ApiResponseEntity?
    fun getApiResponse(id: Int): ApiResponseModel?
    fun getAllApis(): List<ApiModel>
    fun getAllApiResponses(): List<ApiResponseModel>
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

    override fun getApi(id: Int): ApiModel? = transaction(database) {
        ApiEntity.findById(id)?.let {

            val apiResponses: List<ApiResponseEntity> = ApiResponseEntity.find { ApiResponsesTable.api eq it.id }.toList()

            ApiModel(it.id.value, it.name, it.url,
                apiResponses.filter { response -> response.api == it }
                    ?.map { response -> ApiResponseModel(response.id.value, it.name, it.url, response.code, response.type, response.response) }.toList())
        }
    }

    override fun getRawApiResponse(id: Int): ApiResponseEntity? = transaction(database) {
        ApiResponseEntity.findById(id)
    }

    override fun getApiResponse(id: Int): ApiResponseModel? = transaction(database) {
        ApiResponseEntity.findById(id)?.let {
            ApiResponseModel(it.id.value, it.api.name, it.api.url, it.code, it.type, it.response)
        }
    }

    override fun getAllApis(): List<ApiModel> = transaction(database) {
        val apiResponses: List<ApiResponseEntity> = ApiResponseEntity.all().toList()

        ApiEntity.all().toList().map { ApiModel(it.id.value, it.name, it.url,
            apiResponses.filter { response -> response.api == it }
                ?.map { response -> ApiResponseModel(response.id.value, it.name, it.url, response.code, response.type, response.response) }.toList()) }
            .toList()

    }

    override fun getAllApiResponses(): List<ApiResponseModel> = transaction(database) {
        ApiResponseEntity.all().map {
            ApiResponseModel(it.id.value, it.api.name, it.api.url, it.code, it.type, it.response)
        }.toList()
    }

    override fun close() {}

}