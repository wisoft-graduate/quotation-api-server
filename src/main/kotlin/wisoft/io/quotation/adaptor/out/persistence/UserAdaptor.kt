package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(
    val userRepository: UserRepository,
) : GetUserPort, CreateUserPort, UpdateUserPort, DeleteUserPort, GetUserListPort {
    override fun create(user: User): String {
        return userRepository.save(user.toEntity()).id
    }

    override fun getUserById(id: String): User? {
        val userEntity = userRepository.findByIdOrNull(id)
        return userEntity?.toDomain()
    }

    override fun getUserByNickname(nickname: String): User? {
        val userEntity = userRepository.findByNickname(nickname)
        return userEntity?.toDomain()
    }

    override fun updateUser(user: User): String {
        return userRepository.save(user.toEntity()).id
    }

    override fun deleteUser(user: User) {
        userRepository.save(user.toEntity())
    }

    override fun getUserList(nickname: String): List<User> {
        val userEntityList: List<UserEntity> = userRepository.findAllByNicknameContains(nickname)
        return userEntityList.map { it.toDomain() }
    }

    override fun getUserByIdentityInformation(
        id: String,
        question: String,
        answer: String,
    ): User? {
        val userEntity =
            userRepository.findByIdAndIdentityVerificationQuestionAndIdentityVerificationAnswer(
                id,
                question,
                answer,
            )
        return userEntity?.toDomain()
    }
}
