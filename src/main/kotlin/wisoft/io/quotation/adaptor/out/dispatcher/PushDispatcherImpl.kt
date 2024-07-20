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
import java.sql.Timestamp

@Component
class PushDispatcherImpl(
    private val client: OkHttpClient = OkHttpClient(),
    private val logger: mu.KLogger = KotlinLogging.logger { },
    private val objectMapper: ObjectMapper = jacksonObjectMapper(),
    private val apiKey: String = readYmlFile().oneSignal.apiKey,
    private val appId: String = readYmlFile().oneSignal.appId,
) : PushDispatcher {
    override fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean {
        return runCatching {
            val message = "${sendUser}님이 ${tagUser}님을 언급 했습니다."
            val bodyMap =
                mapOf(
                    "app_id" to appId,
                    "headings" to mapOf("en" to "Comment Push"),
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
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody?.contains("errors") == true) {
                    throw PushFailException("sendUser:$sendUser, tagUser :$tagUser, response: $responseBody")
                }
                response.isSuccessful
            }
        }.onFailure {
            logger.error { "sendPushNotification fail: param[sendMember: $sendUser, tagMember: $tagUser]" }
        }.getOrThrow()
    }

    override fun sendAlarmPushNotification(
        userNickname: String,
        userId: String,
        pushTime: Timestamp,
    ): Boolean {
        return runCatching {
            val message = "${userNickname}님, 명언을 확인할 시간입니다. 오늘의 명언을 확인해보세요."
            val bodyMap =
                mapOf(
                    "app_id" to appId,
                    "headings" to mapOf("en" to "Comment Push"),
                    "contents" to mapOf("en" to message),
                    "target_channel" to "push",
                    "include_aliases" to mapOf("external_id" to listOf(userId)),
                    "send_after" to "$pushTime",
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
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody?.contains("errors") == true) {
                    throw PushFailException("userNickname: $userNickname, userId: $userId, pushTime: $pushTime")
                }
                response.isSuccessful
            }
        }.onFailure {
            logger.error { "sendPushNotification fail: param[userNickname: $userNickname, userId :$userId]" }
        }.getOrThrow()
    }
}
