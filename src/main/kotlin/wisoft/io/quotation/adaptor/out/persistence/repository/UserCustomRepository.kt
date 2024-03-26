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

    fun getUserList(request: GetUserListUseCase.GetUserListRequest):List<UserEntity> {
        val parameterMap = mutableMapOf<String, Any>()
        val sql = buildString {
            append("SELECT * FROM account ")

            request.ids?.run {
                append("AND id IN :ids ")
                parameterMap.put("ids", this)
            }
            request.nicknameList?.run {
                append("AND nickname IN :nicknameList")
                parameterMap.put("nicknameList", this)
            }
        }

        return entityManager
            .createNativeQuery(sql, UserEntity::class.java)
            .apply {
                parameterMap.forEach{
                    setParameter(it.key, it.value)
                }
            }
            .resultList as List<UserEntity>
    }
}