package wisoft.io.quotation.application.port.`in`

import java.util.UUID

interface DeleteBookmarkUseCase {

    fun deleteBookmark(id: UUID)
}