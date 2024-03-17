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

    fun findQuotations(request: GetQuotationsUseCase.GetQuotationRequest): List<QuotationEntity> {
        val parameterMap = mutableMapOf<String, Any>()
        val sql = buildString {
            append("SELECT * FROM quotation WHERE 1=1 ")
            request.searchWord?.run {
                append("AND content LIKE :content ")
                parameterMap.put("content", "%$this%")
            }

            request.ids?.run {
                append("AND id in :ids ")
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
                append("offset ${this.offset} ")
                append("limit ${this.limit} ")
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