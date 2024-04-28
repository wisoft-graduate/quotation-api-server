package wisoft.io.quotation.fixture.entity

import org.mindrot.jbcrypt.BCrypt
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import java.sql.Timestamp
import java.time.LocalDateTime
fun getUserEntityFixture(id: String? = null, nickname: String? = null): UserEntity {
    return UserEntity(
        id = id ?: "testUser",
        nickname = nickname ?: "testNickname",
        password = BCrypt.hashpw("password", BCrypt.gensalt()),
        identityVerificationQuestion = "question",
        identityVerificationAnswer = "answer",
        commentAlarm = false,
        quotationAlarm = false,
        createdTime = Timestamp.valueOf(LocalDateTime.now()),
    )
}
