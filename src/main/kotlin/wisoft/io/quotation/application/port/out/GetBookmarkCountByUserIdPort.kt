package wisoft.io.quotation.application.port.out

interface GetBookmarkCountByUserIdPort {

    fun getBookmarkCountByUserId(userId: String): Long
}