package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.Bookmark
import java.util.*

interface GetBookmarkPort {

    fun getBookmark(id: UUID): Bookmark?
}