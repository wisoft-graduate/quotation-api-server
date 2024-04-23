package wisoft.io.quotation.adaptor.out.persistence.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.AuthorEntity
import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import wisoft.io.quotation.application.port.`in`.GetQuotationListUseCase
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.SortDirection

@Repository
class QuotationCustomRepository(
    val queryFactory: SpringDataQueryFactory,
) {
    fun findQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<QuotationEntity> {
        return queryFactory.listQuery<QuotationEntity> {
            select(QuotationEntity::class.java)
            from(entity(QuotationEntity::class))
            join(entity(AuthorEntity::class), on { column(QuotationEntity::authorId).equal(column(AuthorEntity::id)) })

            whereAnd(
                request.searchWord?.run {
                    and(column(QuotationEntity::content).like("%${request.searchWord}%"))
                        .or(column(AuthorEntity::name).like("%${request.searchWord}%"))
                },
                request.ids?.run { and(column(QuotationEntity::id).`in`(request.ids)) },
            )

            request.sortTarget?.let { sortTarget ->
                val sortColumn =
                    when (sortTarget) {
                        QuotationSortTarget.LIKE -> column(QuotationEntity::likeCount)
                        QuotationSortTarget.SHARE -> column(QuotationEntity::shareCount)
                    }

                if (request.sortDirection == SortDirection.DESC) {
                    orderBy(sortColumn.desc())
                } else {
                    orderBy(sortColumn.asc())
                }
            }

            request.paging?.run {
                offset((this.page - 1) * count)
                limit(this.count)
            }
        }
    }

//    fun findQuotationList(request: GetQuotationsUseCase.GetQuotationListRequest): List<QuotationEntity> {
//        val parameterMap = mutableMapOf<String, Any>()
//        val sql = buildString {
//            append("SELECT q.* FROM quotation q, author a WHERE q.author_id = a.id ")
//            request.searchWord?.run {
//                append("AND content LIKE :searchWord ")
//                append("OR a.name LIKE :searchWord ")
//                parameterMap.put("searchWord", "%$this%")
//            }
//
//            request.ids?.run {
//                append("AND q.id IN :ids ")
//                parameterMap.put("ids", this)
//            }
//
//            request.sortTarget?.run {
//                val sortTarget = when (this) {
//                    QuotationSortTarget.LIKE -> "like_count "
//                    QuotationSortTarget.SHARE -> "share_count "
//                }
//                append("ORDER BY $sortTarget ")
//
//                request.sortDirection?.run {
//                    append("$this ")
//                }
//            }
//
//            request.paging?.run {
//                append("offset ${(this.page - 1) * count} ")
//                append("limit ${this.count} ")
//            }
//        }
//
//        return entityManager
//            .createNativeQuery(sql, QuotationEntity::class.java)
//            .apply {
//                parameterMap.forEach {
//                    setParameter(it.key, it.value)
//                }
//            }
//            .resultList as List<QuotationEntity>
//    }
}
