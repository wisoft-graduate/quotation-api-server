package wisoft.io.quotation.adaptor.out.dispatcher

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import wisoft.io.quotation.exception.error.PushFailException
import wisoft.io.quotation.util.JWTUtil.readYmlFile

@Component
class PushDispatcher(
    private val client: OkHttpClient = OkHttpClient(),
    private val logger: mu.KLogger = KotlinLogging.logger { },
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
    private val apiKey: String = readYmlFile().oneSignal.apiKey,
    private val appId: String = readYmlFile().oneSignal.appId,
) : PushDispatcherImpl {
    override fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean {
        return runCatching {
            val message = "${sendUser}님이 ${tagUser}닝을 언급하셨습니다."
            val bodyMap =
                mapOf(
                    "app_id" to appId,
                    "contents" to mapOf("en" to message),
                    "target_channel" to "push",
                    "include_aliases" to mapOf("external_id" to listOf(tagUserId)),
                )

            val json = objectMapper.writeValueAsString(bodyMap)

            val body =
                json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request =
                Request.Builder()
                    .url("https://api.onesignal.com/notifications")
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Basic $apiKey")
                    .addHeader("content-type", "application/json")
                    .post(body)
                    .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw PushFailException("sendUser:$sendUser, tagUser :$tagUser")
                }
                response.isSuccessful
            }
        }.onFailure {
            logger.error { "sendPushNotification fail: param[sendMember: $sendUser, tagMember: $tagUser]" }
        }.getOrThrow()
    }
}
