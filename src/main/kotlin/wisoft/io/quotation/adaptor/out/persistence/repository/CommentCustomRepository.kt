package wisoft.io.quotation.adaptor.out.persistence.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.CommentEntity
import java.util.*

@Repository
class CommentCustomRepository(
    val queryFactory: SpringDataQueryFactory,
) {
    fun findCommentList(
        commentIds: List<UUID>?,
        quotationId: UUID?,
        parentId: UUID?,
    ): List<CommentEntity> {
        return queryFactory.listQuery<CommentEntity> {
            select(entity(CommentEntity::class))
            from(entity(CommentEntity::class))

            whereAnd(
                commentIds?.let {
                    and(column(CommentEntity::id).`in`(it))
                },
                quotationId?.let {
                    and(column(CommentEntity::quotationId).equal(it))
                },
                parentId?.let {
                    and(column(CommentEntity::parentId).equal(it))
                },
            )
        }
    }
}
