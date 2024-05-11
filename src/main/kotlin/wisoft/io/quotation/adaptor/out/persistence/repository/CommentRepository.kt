package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.CommentEntity
import java.util.UUID

@Repository
interface CommentRepository : JpaRepository<CommentEntity, UUID> {
    fun findByParentId(parentId: UUID): List<CommentEntity>
}
