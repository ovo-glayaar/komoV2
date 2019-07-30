package org.ovo.mockserver.user.dao

import api.dao.ApiDaoFacade
import api.model.ApiResponseTable
import api.model.ApiTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.ovo.mockserver.user.model.UserTokenEntity
import user.model.UserApiStatesTable
import user.model.UserEntity
import user.model.UserTable
import user.model.UserTokensTable

interface UserDaoFacade {
    fun init()
    fun createUser(username: String, password: String, name: String, apiStates: List<Int>)
    fun updateUser(id: Int, username: String, password: String, name: String, token: String, apiStates: List<Int>)
    fun deleteUser(id: Int)
    fun getUser(id: Int): UserEntity?
    fun getAllUsers(): List<UserEntity>
    fun getResponseByUser(token: String, url: String): String?
}

class UserDaoFacadeImpl(private val database: Database,
                        private val apiDaoFacade: ApiDaoFacade) : UserDaoFacade {
    override fun getResponseByUser(token: String, url: String): String? = transaction(database) {
        val result = UserTable.join(UserTokensTable, JoinType.INNER, additionalConstraint = {UserTable.id eq UserTokensTable.user})
                .join(UserApiStatesTable, JoinType.INNER, additionalConstraint = {UserTable.id eq UserApiStatesTable.user})
                .join(ApiResponseTable, JoinType.INNER, additionalConstraint = {UserApiStatesTable.apiresponse eq ApiResponseTable.id})
                .join(ApiTable, JoinType.INNER, additionalConstraint = {ApiResponseTable.api eq ApiTable.id})
                .slice(UserTokensTable.token, ApiTable.url, ApiResponseTable.response)
                .select { UserTokensTable.token eq token and (ApiTable.url eq url) }.firstOrNull()

        if(result != null) result[ApiResponseTable.response] else null
    }

    override fun init() = transaction(database) {
        SchemaUtils.create(UserTable)
    }

    override fun createUser(username: String, password: String, name: String, apiStates: List<Int>) = transaction(database) {
        UserEntity.new {
            this.username = username
            this.password = password
            this.name = name

            apiStates.mapNotNull { apiDaoFacade.getApiResponse(it) }.toList().let {
                this.apiStates = SizedCollection(it)
            }
        }

        Unit
    }

    override fun updateUser(id: Int, username: String, password: String, name: String, token: String, apiStates: List<Int>) = transaction(database) {
        UserEntity.findById(id)?.apply {
            this.username = username
            this.password = password
            this.name = name

            apiStates.mapNotNull { apiDaoFacade.getApiResponse(it) }.toList().let {
                this.apiStates = SizedCollection(it)
            }
        }?.also {
            UserTokenEntity.new {
                this.user = it
                this.token = token
            }
        }

        Unit
    }

    override fun deleteUser(id: Int) = transaction(database) {
        UserTable.deleteWhere { UserTable.id eq id }
        Unit
    }

    override fun getUser(id: Int): UserEntity? = transaction(database) {
        UserEntity.findById(id)
    }

    override fun getAllUsers(): List<UserEntity> = transaction(database) {
        UserEntity.all().toList()
    }

}