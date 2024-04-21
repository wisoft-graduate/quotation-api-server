package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {
    fun findByNickname(nickname: String): UserEntity?

    fun findAllByNicknameContains(nickname: String): List<UserEntity>

    fun findByIdAndIdentityVerificationQuestionAndIdentityVerificationAnswer(
        nickname: String,
        identityVerificationQuestion: String,
        identityVerificationAnswer: String,
    ): UserEntity?
}
