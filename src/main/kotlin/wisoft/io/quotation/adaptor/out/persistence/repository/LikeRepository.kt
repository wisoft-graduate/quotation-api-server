package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import wisoft.io.quotation.adaptor.out.persistence.entity.LikeEntity
import java.util.*

interface LikeRepository : JpaRepository<LikeEntity, UUID> {
    fun countAllByUserId(userId: String): Long
}