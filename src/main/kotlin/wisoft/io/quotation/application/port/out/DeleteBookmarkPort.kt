package wisoft.io.quotation.application.port.out

import java.util.UUID

interface DeleteBookmarkPort {
    fun deleteBookmark(id: UUID)
}
