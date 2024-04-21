package wisoft.io.quotation.application.port.out

interface GetLikeListPort {
    fun getLikeListCountByUserId(userId: String): Long
}
