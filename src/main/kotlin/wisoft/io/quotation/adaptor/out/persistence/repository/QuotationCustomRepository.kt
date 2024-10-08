package wisoft.io.quotation.adaptor.out.persistence.repository

import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationRankView
import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationViewEntity
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationRankUseCase
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.RankProperty

@Repository
class QuotationCustomRepository(
    val queryFactory: SpringDataQueryFactory,
    val entityManager: EntityManager,
) {
    fun findQuotationRank(request: GetQuotationRankUseCase.GetQuotationRankRequest): List<QuotationRankView> {
        val sql =
            buildString {
                append("SELECT id, ")
                when (request.rankProperty) {
                    RankProperty.LIKE -> {
                        append("ROW_NUMBER() OVER (ORDER BY like_count DESC) AS like_rank, like_count, ")
                    }

                    RankProperty.SHARE -> {
                        append("ROW_NUMBER() OVER (ORDER BY share_count DESC) AS share_rank, share_count, ")
                    }
                }
                append("background_image_path ")
                append("FROM quotation WHERE 1=1 ")
                request.ids?.let {
                    append("AND id IN :ids ")
                }
                request.paging?.let {
                    append("OFFSET ${(it.page - 1) * it.count} ")
                    append("LIMIT ${it.count} ")
                }
            }
        return entityManager.createNativeQuery(sql, QuotationRankView::class.java)
            .apply { request.ids?.let { setParameter("ids", it) } }
            .resultList as List<QuotationRankView>
    }

    fun findQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<QuotationViewEntity> {
        val parameterMap = mutableMapOf<String, Any>()
        val sql =
            buildString {
                append("SELECT q.id, q.author_id, q.content, q.comment_count, q.like_count, q.share_count, ")
                append("q.created_time, q.last_modified_time, q.background_image_path, ")
                if (request.rankProperty != null) {
                    when (request.rankProperty) {
                        RankProperty.LIKE -> {
                            append("ROW_NUMBER() OVER (ORDER BY like_count DESC) AS like_rank ")
                        }

                        RankProperty.SHARE -> {
                            append("ROW_NUMBER() OVER (ORDER BY share_count DESC) AS share_rank ")
                        }
                    }
                } else {
                    append("0 ")
                }
                append("FROM quotation q, author a ")
                append("WHERE q.author_id = a.id ")

                request.searchWord?.run {
                    append("AND content ILIKE :searchWord ")
                    append("OR a.name ILIKE :searchWord ")
                    parameterMap.put("searchWord", "%$this%")
                }

                request.ids?.run {
                    append("AND q.id IN :ids ")
                    parameterMap.put("ids", this)
                }

                request.sortTarget?.run {
                    val sortTarget =
                        when (this) {
                            QuotationSortTarget.LIKE -> "like_count "
                            QuotationSortTarget.SHARE -> "share_count "
                        }
                    append("ORDER BY $sortTarget ")

                    request.sortDirection?.run {
                        append("$this ")
                    }
                }

                request.paging?.run {
                    append("offset ${(this.page - 1) * count} ")
                    append("limit ${this.count} ")
                }
            }

        return entityManager.createNativeQuery(sql, QuotationViewEntity::class.java)
            .apply {
                parameterMap.forEach {
                    setParameter(it.key, it.value)
                }
            }
            .resultList as List<QuotationViewEntity>
    }

//    fun findQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<QuotationEntity> {
//        return queryFactory.listQuery<QuotationEntity> {
//            select(QuotationEntity::class.java)
//            from(entity(QuotationEntity::class))
//            join(entity(AuthorEntity::class), on { column(QuotationEntity::authorId).equal(column(AuthorEntity::id)) })
//
//            whereAnd(
//                request.searchWord?.run {
//                    and(column(QuotationEntity::content).like("%${request.searchWord}%"))
//                        .or(column(AuthorEntity::name).like("%${request.searchWord}%"))
//                },
//                request.ids?.run { and(column(QuotationEntity::id).`in`(request.ids)) },
//            )
//
//            request.sortTarget?.let { sortTarget ->
//                val sortColumn =
//                    when (sortTarget) {
//                        QuotationSortTarget.LIKE -> column(QuotationEntity::likeCount)
//                        QuotationSortTarget.SHARE -> column(QuotationEntity::shareCount)
//                    }
//
//                if (request.sortDirection == SortDirection.DESC) {
//                    orderBy(sortColumn.desc())
//                } else {
//                    orderBy(sortColumn.asc())
//                }
//            }
//
//            request.paging?.run {
//                offset((this.page - 1) * count)
//                limit(this.count)
//            }
//        }
//    }

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
