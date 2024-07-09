package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.mapeer.BookmarkMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.BookmarkRepository
import wisoft.io.quotation.application.port.out.bookmark.*
import wisoft.io.quotation.domain.Bookmark
import java.util.*

@Component
class BookmarkAdaptor(val bookmarkRepository: BookmarkRepository, val bookmarkMapper: BookmarkMapper) :
    CreateBookmarkPort,
    GetBookmarkListPort,
    GetBookmarkPort,
    UpdateBookmarkPort,
    DeleteBookmarkPort {
    override fun createBookmark(bookmark: Bookmark): UUID {
        return bookmarkRepository.save(bookmarkMapper.toEntity(bookmark)).id
    }

    override fun updateBookmark(bookmark: Bookmark): UUID {
        return bookmarkRepository.save(bookmarkMapper.toEntity(bookmark)).id
    }

    override fun deleteBookmark(id: UUID) {
        return bookmarkRepository.deleteById(id)
    }

    override fun getBookmarkListCountByUserId(userId: String): Long {
        return bookmarkRepository.countAllByUserId(userId)
    }

    override fun getBookmarkList(userId: String): List<Bookmark> {
        return bookmarkRepository.findByUserId(userId).map { bookmarkMapper.toDomain(it) }
    }

    override fun getBookmark(id: UUID): Bookmark? {
        return bookmarkRepository.findByIdOrNull(id)?.let { bookmarkMapper.toDomain(it) }
    }
}
