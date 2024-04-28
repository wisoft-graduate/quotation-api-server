package wisoft.io.quotation.fixture.entity

import org.mindrot.jbcrypt.BCrypt
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import java.sql.Timestamp
import java.time.LocalDateTime

fun getUserEntityFixture(): UserEntity {
    return UserEntity(
        id = "testUser",
        password = BCrypt.hashpw("password", BCrypt.gensalt()),
        nickname = "testNickname",
        identityVerificationQuestion = "question",
        identityVerificationAnswer = "answer",
        commentAlarm = false,
        quotationAlarm = false,
        createdTime = Timestamp.valueOf(LocalDateTime.now()),
    )
}

fun getUserEntityFixture(id: String): UserEntity {
    return UserEntity(
        id = id,
        password = BCrypt.hashpw("password", BCrypt.gensalt()),
        nickname = "testNickname",
        identityVerificationQuestion = "question",
        identityVerificationAnswer = "answer",
        commentAlarm = false,
        quotationAlarm = false,
        createdTime = Timestamp.valueOf(LocalDateTime.now()),
    )
}
