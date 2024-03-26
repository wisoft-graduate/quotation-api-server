package wisoft.io.quotation.adaptor.out.persistence.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.application.port.`in`.GetUserListUseCase

@Repository
class UserCustomRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<UserEntity> {
        val parameterMap = mutableMapOf<String, Any>()
        var andCount = 0;
        val sql = buildString {
            append("SELECT * FROM account ")

            request.ids?.run {
                append("WHERE id IN :ids ")
                parameterMap.put("ids", this)
                andCount += 1
            }
            request.nicknameList?.run {
                if (andCount.equals(0)) {
                    append("WHERE nickname IN :nicknameList ")
                } else {
                    append("AND nickname IN :nicknameList ")
                    andCount += 1
                }
                parameterMap.put("nicknameList", this)
            }
        }

        return entityManager
            .createNativeQuery(sql, UserEntity::class.java)
            .apply {
                parameterMap.forEach {
                    setParameter(it.key, it.value)
                }
            }
            .resultList as List<UserEntity>
    }
}