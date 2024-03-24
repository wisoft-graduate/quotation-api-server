package wisoft.io.quotation.adaptor.out.persistence.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.domain.QuotationSortTarget

@Repository
class QuotationCustomRepository(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    fun findQuotations(request: GetQuotationsUseCase.GetQuotationsRequest): List<QuotationEntity> {
        val parameterMap = mutableMapOf<String, Any>()
        val sql = buildString {
            append("SELECT q.* FROM quotation q, author a WHERE q.author_id = a.id ")
            request.searchWord?.run {
                append("AND content LIKE :searchWord ")
                append("OR a.name LIKE :searchWord ")
                parameterMap.put("searchWord", "%$this%")
            }

            request.ids?.run {
                append("AND q.id IN :ids ")
                parameterMap.put("ids", this)
            }

            request.sortTarget?.run {
                val sortTarget = when (this) {
                    QuotationSortTarget.LIKE -> "like_count "
                    QuotationSortTarget.SHARE -> "share_count "
                }
                append("ORDER BY $sortTarget ")

                request.sortDirection?.run {
                    append("$this ")
                }
            }

            request.paging?.run {
                append("offset ${(this.page -1) * count} ")
                append("limit ${this.count} ")
            }
        }

        return entityManager
            .createNativeQuery(sql, QuotationEntity::class.java)
            .apply {
                parameterMap.forEach {
                    setParameter(it.key, it.value)
                }
            }
            .resultList as List<QuotationEntity>
    }
}