package org.ovo.mockserver.user.dao

import api.dao.ApiDaoFacade
import api.model.ApiResponseEntity
import api.model.ApiResponsesTable
import api.model.ApisTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.ovo.mockserver.api.model.ApiResponseModel
import org.ovo.mockserver.user.model.UserModel
import org.ovo.mockserver.user.model.UserTokenEntity
import user.model.UserApiStatesTable
import user.model.UserEntity
import user.model.UsersTable
import user.model.UserTokensTable

interface UserDaoFacade {
    fun init()
    fun createUser(username: String, password: String, name: String, phone: String, apiStates: List<Int>)
    fun updateUser(id: Int, username: String, password: String, name: String, phone: String, token: String, apiStates: List<Int>)
    fun deleteUser(id: Int)
    fun getUser(id: Int): UserModel?
    fun getUserByToken(token: String): UserModel?
    fun getAllUsers(): List<UserModel>
    fun getResponseByUser(token: String, url: String): String?
    fun getResponseByPhone(phone: String, url: String): String?
    fun updateTokenByPhone(phone: String, token: String)
}

class UserDaoFacadeImpl(private val database: Database,
                        private val apiDaoFacade: ApiDaoFacade) : UserDaoFacade {
    
    override fun getResponseByUser(token: String, url: String): String? = transaction(database) {
        val result = UsersTable.join(UserTokensTable, JoinType.INNER, additionalConstraint = {UsersTable.id eq UserTokensTable.user})
                .join(UserApiStatesTable, JoinType.INNER, additionalConstraint = {UsersTable.id eq UserApiStatesTable.user})
                .join(ApiResponsesTable, JoinType.INNER, additionalConstraint = {UserApiStatesTable.apiresponse eq ApiResponsesTable.id})
                .join(ApisTable, JoinType.INNER, additionalConstraint = {ApiResponsesTable.api eq ApisTable.id})
                .slice(UserTokensTable.token, ApisTable.url, ApiResponsesTable.response)
                .select { UserTokensTable.token eq token and (ApisTable.url eq url) }.firstOrNull()

        if(result != null) result[ApiResponsesTable.response] else null
    }

    override fun getResponseByPhone(phone: String, url: String): String? = transaction(database) {
        val result = UsersTable.join(UserApiStatesTable, JoinType.INNER, additionalConstraint = {UsersTable.id eq UserApiStatesTable.user})
            .join(ApiResponsesTable, JoinType.INNER, additionalConstraint = {UserApiStatesTable.apiresponse eq ApiResponsesTable.id})
            .join(ApisTable, JoinType.INNER, additionalConstraint = {ApiResponsesTable.api eq ApisTable.id})
            .slice(UsersTable.phone, ApisTable.url, ApiResponsesTable.response)
            .select { UsersTable.phone eq phone and (ApisTable.url eq url) }.firstOrNull()

        if(result != null) result[ApiResponsesTable.response] else null
    }

    override fun init() = transaction(database) {
        SchemaUtils.create(UsersTable)
    }

    override fun createUser(username: String, password: String, name: String, phone: String, apiStates: List<Int>) = transaction(database) {
        UserEntity.new {
            this.username = username
            this.password = password
            this.name = name
            this.phone = phone

            apiStates.mapNotNull { apiDaoFacade.getRawApiResponse(it) }.toList().let {
                this.apiStates = SizedCollection(it)
            }
        }

        Unit
    }

    override fun updateTokenByPhone(phone: String, token: String) = transaction(database) {

        UserEntity.find{ UsersTable.phone eq phone }.firstOrNull()?.also  {
            UserTokenEntity.new {
                this.user = it
                this.token = token
            }
        }

        Unit
    }

    override fun updateUser(id: Int, username: String, password: String, name: String, phone: String, token: String, apiStates: List<Int>) = transaction(database) {
        UserEntity.findById(id)?.apply {
            this.username = username
            this.password = password
            this.name = name

            apiStates.mapNotNull { apiDaoFacade.getRawApiResponse(it) }.toList().let {
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
        UserEntity.find { UsersTable.id eq id }.forEach { it.delete() }
        Unit
    }



    override fun getUser(id: Int): UserModel? = transaction(database) {

        UserEntity.findById(id)?.let {

            val userTokens: List<UserTokenEntity> = UserTokenEntity.find { UserTokensTable.user.eq(it.id) }.toList()

            UserModel(it.id.value, it.username, it.name, it.phone,
                userTokens.sortedByDescending { token -> token.id.value }.firstOrNull { token -> token.user == it }?.token.orEmpty(),
                it.apiStates.map { api -> ApiResponseModel(api.id.value, api.api.name, api.api.url, api.code, api.type, api.response) }.toList())
        }

    }

    override fun getUserByToken(token: String): UserModel? = transaction(database) {

        UserTokenEntity.find{ UserTokensTable.token eq token }.firstOrNull()?.let {
            UserModel(it.user.id.value, it.user.username, it.user.name,
                it.user.phone, it.token,
                it.user.apiStates.map { api -> ApiResponseModel(api.id.value, api.api.name, api.api.url, api.code, api.type, api.response) }.toList())
        }

    }

    override fun getAllUsers(): List<UserModel> = transaction(database) {
        val userTokens: List<UserTokenEntity> = UserTokenEntity.all().toList()

        UserEntity.all().toList().map { UserModel(it.id.value, it.username, it.name, it.phone,
            userTokens.sortedByDescending { token -> token.id.value }.firstOrNull { token -> token.user == it }?.token.orEmpty(),
            it.apiStates.map { api -> ApiResponseModel(api.id.value, api.api.name, api.api.url, api.code, api.type, api.response) }.toList()) }.toList()
    }

}