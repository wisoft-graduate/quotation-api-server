package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.push.PushQuotationAlarmUseCase
import wisoft.io.quotation.application.port.out.push.PushAlarmNotificationPort
import wisoft.io.quotation.application.port.out.user.GetActiveUserListPort
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class SchedulerService(
    val pushAlarmNotificationPort: PushAlarmNotificationPort,
    val getActiveUserListPort: GetActiveUserListPort,
) : PushQuotationAlarmUseCase {
    val logger = KotlinLogging.logger {}

    // 매일 자정을 기준으로, 당일의 Push Notification을 제공함
    @Scheduled(cron = "0 0 0 * * ?")
    override fun pushQuotationAlarm(): Int {
        return runCatching {
            val userList = getActiveUserListPort.getActiveUserList()

            var count = 0
            userList.forEach { user ->
                user.quotationAlarmTimes.forEach {
                    val localDateTime = it.toLocalDateTime()
                    val localTime = localDateTime.toLocalTime()
                    val todayAlarmTime = LocalDateTime.of(LocalDate.now(), localTime)

                    pushAlarmNotificationPort.sendAlarmPushNotification(
                        userId = user.id,
                        userNickname = user.nickname,
                        pushTime = Timestamp.valueOf(todayAlarmTime),
                    )
                    count++
                }
            }

            count
        }.onFailure {
            logger.error { "pushQuotationAlarm fail" }
        }.getOrThrow()
    }
}
