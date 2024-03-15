package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.domain.Example
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.out.FindUserPort
import wisoft.io.quotation.application.port.out.SaveUserPort
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(val userRepository: UserRepository) : SaveUserPort, FindUserPort {

    override fun save(user: User): String {
        return userRepository.save(UserEntity.from(user)).id
    }

    override fun findByIdOrNull(id: String): User {
        val userEntity = userRepository.findByIdOrNull(id) ?: throw RuntimeException()
        return UserEntity.to(userEntity)
    }

    override fun findLeaveUsersCount(): Long {
        return userRepository.countByNicknameStartingWith("leave#")
    }

    override fun existUser(id: String): Boolean {
        return userRepository.existsById(id)
    }

    override fun existUserByNickname(nickname: String): Boolean {
        return userRepository.existsByNickname(nickname)
    }
}