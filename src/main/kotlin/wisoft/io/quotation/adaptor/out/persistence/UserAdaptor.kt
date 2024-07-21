package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.mapeer.UserMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.UserCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.user.GetUserListUseCase
import wisoft.io.quotation.application.port.out.user.*
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(
    val userRepository: UserRepository,
    val userCustomRepository: UserCustomRepository,
    val userMapper: UserMapper,
) : GetUserPort, CreateUserPort, UpdateUserPort, DeleteUserPort, GetUserListPort, GetActiveUserListPort {
    override fun create(user: User): String {
        return userRepository.save(userMapper.toEntity(user)).id
    }

    override fun getUserById(id: String): User? {
        val userEntity = userRepository.findByIdOrNull(id)
        return userEntity?.let { userMapper.toDomain(it) }
    }

    override fun getUserByNickname(nickname: String): User? {
        val userEntity = userRepository.findByNickname(nickname)
        return userEntity?.let { userMapper.toDomain(it) }
    }

    override fun updateUser(user: User): String {
        return userRepository.save(userMapper.toEntity(user)).id
    }

    override fun deleteUser(user: User) {
        userRepository.save(userMapper.toEntity(user))
    }

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<User> {
        val userList = userCustomRepository.getUserList(request)
        return userList.map { userMapper.toDomain(it) }
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
        return userEntity?.let { userMapper.toDomain(it) }
    }

    override fun getActiveUserList(): List<User> {
        val userList = userRepository.findAllByNicknameNotContainsAndQuotationAlarmIsTrue("leaved#")
        return userList.map { userMapper.toDomain(it) }
    }
}
