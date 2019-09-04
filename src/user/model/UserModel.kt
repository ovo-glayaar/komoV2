package org.ovo.mockserver.user.model

import org.ovo.mockserver.api.model.ApiResponseModel

data class UserModel(val id: Int, val username: String,
                     val name: String, val phone: String, val token: String,
                     val apiResponses: List<ApiResponseModel>)
