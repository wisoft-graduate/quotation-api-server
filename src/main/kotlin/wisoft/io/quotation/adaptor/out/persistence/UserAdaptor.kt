package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.GetUserListUseCase
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

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<User> {
        val userList: List<UserEntity> =
            when {
                request.searchNickname != null -> userRepository.findAllByNicknameContains(request.searchNickname)
                request.nickname != null -> userRepository.findAllByNickname(request.nickname)
                request.id != null -> userRepository.findAllById(request.id)
                else -> emptyList() // 모든 옵션이 없는 경우 빈 리스트 반환
            }
        return userList.map { it.toDomain() }
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
