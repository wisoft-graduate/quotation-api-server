package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.UserCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.GetUserListUseCase
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(
    val userRepository: UserRepository,
    val userCustomRepository: UserCustomRepository
) : SaveUserPort, GetUserByIdPort,
    GetUserListPort {

    override fun save(user: User): String {
        return userRepository.save(user.toEntity()).id
    }

    override fun getByIdOrNull(id: String): User? {
        val userEntity = userRepository.findByIdOrNull(id)
        return userEntity?.toDomain()
    }

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<User> {
        val userList = userCustomRepository.getUserList(request)
        return userList.map { it.toDomain() }
    }

}