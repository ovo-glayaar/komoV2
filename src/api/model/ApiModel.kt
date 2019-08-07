package org.ovo.mockserver.api.model

class ApiModel (val id: Int,
                val name: String,
                val url:String,
                val apiResponses: List<ApiResponseModel>)