package wisoft.io.quotation.application.port.out.bookmark

import wisoft.io.quotation.domain.Bookmark
import java.util.UUID

interface CreateBookmarkPort {
    fun createBookmark(bookmark: Bookmark): UUID
}
