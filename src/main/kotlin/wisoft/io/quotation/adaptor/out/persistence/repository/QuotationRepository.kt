package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import java.util.UUID

@Repository
interface QuotationRepository : JpaRepository<QuotationEntity, UUID> {
    @Modifying
    @Query("UPDATE QuotationEntity q SET q.commentCount = q.commentCount + 1 WHERE q.id = :id")
    fun incrementCommentCount(id: UUID)

    @Modifying
    @Query("UPDATE QuotationEntity q SET q.commentCount = q.commentCount - 1 WHERE q.id = :id")
    fun decrementCommentCount(id: UUID)

    @Modifying
    @Query(value = "UPDATE QuotationEntity q SET q.likeCount = q.likeCount + 1 WHERE q.id = :id")
    fun incrementLikeCount(id: UUID)

    @Modifying
    @Query(value = "UPDATE QuotationEntity q SET q.likeCount = q.likeCount - 1 WHERE q.id = :id")
    fun decrementLikeCount(id: UUID)

    @Modifying
    @Query(value = "UPDATE QuotationEntity q SET q.shareCount = q.shareCount + 1 WHERE q.id = :id")
    fun incrementShareCount(id: UUID)
}
