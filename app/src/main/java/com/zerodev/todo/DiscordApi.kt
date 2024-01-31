package com.zerodev.todo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface DiscordApi {
    @POST("webhooks/{webhookId}/{webhookToken}")
    fun sendMessage(
        @Path("webhookId") webhookId: String,
        @Path("webhookToken") webhookToken: String,
        @Body message: WebhookData
    ): Call<ResponseBody>
}
