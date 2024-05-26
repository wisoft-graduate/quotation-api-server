package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.UserCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.user.GetUserListUseCase
import wisoft.io.quotation.application.port.out.user.*
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(
    val userRepository: UserRepository,
    val userCustomRepository: UserCustomRepository,
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
        val userList = userCustomRepository.getUserList(request)
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
