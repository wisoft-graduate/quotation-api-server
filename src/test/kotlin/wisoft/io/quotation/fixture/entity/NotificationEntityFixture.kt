package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.NotificationEntity
import java.util.*

fun getNotificationEntityFixture(): NotificationEntity =
    NotificationEntity(
        commenterId = "commenter",
        commentedUserId = "commentedUser",
        commentId = UUID.randomUUID(),
        alarmCheck = false,
    )
