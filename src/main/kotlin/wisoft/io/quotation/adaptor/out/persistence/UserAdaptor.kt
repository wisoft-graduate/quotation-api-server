package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(
    val userRepository: UserRepository,
) : SaveUserPort, GetUserByIdPort,
    GetUserByNicknamePort{

    override fun save(user: User): String {
        return userRepository.save(user.to()).id
    }

    override fun getByIdOrNull(id: String): User? {
        val userEntity = userRepository.findByIdOrNull(id)
        return userEntity?.to()
    }

    override fun getByNicknameOrNull(nickname: String): User? {
        val userEntity = userRepository.findByNickname(nickname)
        return userEntity?.to()
    }
}