package wisoft.io.quotation.adaptor.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import wisoft.io.quotation.adaptor.out.persistence.entity.BookmarkEntity
import java.util.*

@Repository
interface BookmarkRepository: JpaRepository<BookmarkEntity, UUID>{
    fun countAlLByUserId(userId: String): Long
}
