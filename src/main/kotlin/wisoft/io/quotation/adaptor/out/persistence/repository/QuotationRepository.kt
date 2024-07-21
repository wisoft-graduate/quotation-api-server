package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import java.util.UUID

@Repository
interface QuotationRepository : JpaRepository<QuotationEntity, UUID> {
    @Modifying
    @Query(value = "UPDATE QuotationEntity q SET q.likeCount = q.likeCount + 1 WHERE q.id = :id")
    fun incrementLikeCount(
        @Param("id") id: UUID,
    )

    @Modifying
    @Query(value = "UPDATE QuotationEntity q SET q.likeCount = q.likeCount - 1 WHERE q.id = :id")
    fun decrementLikeCount(
        @Param("id") id: UUID,
    )
}
