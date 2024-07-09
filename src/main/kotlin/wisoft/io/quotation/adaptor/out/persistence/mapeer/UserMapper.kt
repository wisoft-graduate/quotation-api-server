package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.domain.User

@Component
class UserMapper : Mapper<UserEntity, User> {
    override fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            password = entity.password,
            nickname = entity.nickname,
            profilePath = entity.profilePath,
            favoriteAuthor = entity.favoriteAuthor,
            favoriteQuotation = entity.favoriteQuotation,
            commentAlarm = entity.commentAlarm,
            quotationAlarm = entity.quotationAlarm,
            quotationAlarmTimes = entity.quotationAlarmTimes.toList(),
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
            identityVerificationQuestion = entity.identityVerificationQuestion,
            identityVerificationAnswer = entity.identityVerificationAnswer,
        )
    }

    override fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            password = domain.password,
            nickname = domain.nickname,
            profilePath = domain.profilePath,
            favoriteAuthor = domain.favoriteAuthor,
            favoriteQuotation = domain.favoriteQuotation,
            commentAlarm = domain.commentAlarm,
            quotationAlarm = domain.quotationAlarm,
            quotationAlarmTimes = domain.quotationAlarmTimes,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
            identityVerificationQuestion = domain.identityVerificationQuestion,
            identityVerificationAnswer = domain.identityVerificationAnswer,
        )
    }
}
