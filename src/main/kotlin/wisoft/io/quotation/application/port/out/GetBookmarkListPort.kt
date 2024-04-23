package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.Bookmark

interface GetBookmarkListPort {
    fun getBookmarkList(userId: String): List<Bookmark>

    fun getBookmarkListCountByUserId(userId: String): Long
}
