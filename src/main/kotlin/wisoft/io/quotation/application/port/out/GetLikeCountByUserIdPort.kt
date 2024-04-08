package wisoft.io.quotation.application.port.out

interface GetLikeCountByUserIdPort {

    fun getLikeCountByUserId(userId: String): Long
}