package wisoft.io.quotation.application.port.`in`.bookmark

import java.util.UUID

interface DeleteBookmarkUseCase {
    fun deleteBookmark(id: UUID)
}
