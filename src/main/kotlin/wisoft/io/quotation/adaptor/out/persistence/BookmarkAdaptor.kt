package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.BookmarkRepository
import wisoft.io.quotation.application.port.out.CreateBookmarkPort
import wisoft.io.quotation.domain.Bookmark
import java.util.*

@Component
class BookmarkAdaptor(val bookmarkRepository: BookmarkRepository): CreateBookmarkPort {

    override fun createBookmark(bookmark: Bookmark): UUID {
        return bookmarkRepository.save(bookmark.toEntity()).id
    }

}