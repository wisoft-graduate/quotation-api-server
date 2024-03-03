package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.out.SaveUserPort
import wisoft.io.quotation.domain.User

@Component
class UserAdaptor(val userRepository: UserRepository): SaveUserPort {

    override fun save(user: User): String {
        return userRepository.save(UserEntity.from(user)).id
    }

}