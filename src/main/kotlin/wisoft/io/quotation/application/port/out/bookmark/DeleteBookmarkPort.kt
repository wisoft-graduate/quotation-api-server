package wisoft.io.quotation.application.port.out.bookmark

import java.util.UUID

interface DeleteBookmarkPort {
    fun deleteBookmark(id: UUID)
}
