package wisoft.io.quotation.adaptor.out.persistence.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.application.port.`in`.user.GetUserListUseCase

@Repository
class UserCustomRepository(
    val queryFactory: SpringDataQueryFactory,
) {
    fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<UserEntity> {
        return queryFactory.listQuery<UserEntity> {
            select(UserEntity::class.java)
            from(entity(UserEntity::class))

            whereAnd(
                request.searchNickname?.run {
                    and(column(UserEntity::nickname).like("%${request.searchNickname}%"))
                },
                request.id?.run {
                    and(column(UserEntity::id).equal(request.id))
                },
                request.nickname?.run {
                    and(column(UserEntity::nickname).equal(request.nickname))
                },
            )
        }
    }
}
