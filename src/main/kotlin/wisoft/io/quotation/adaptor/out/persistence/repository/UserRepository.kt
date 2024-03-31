package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity

@Repository
interface UserRepository: JpaRepository<UserEntity, String> {
    fun findAllByNickname(nickname: String): List<UserEntity>

    fun findAllById(id: String): List<UserEntity>

}