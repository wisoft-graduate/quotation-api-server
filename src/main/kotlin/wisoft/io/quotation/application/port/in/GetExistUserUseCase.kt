package wisoft.io.quotation.application.port.`in`

interface GetExistUserUseCase {

    fun getExistUser(id: String, nickname: String): Boolean

    data class GetExistUserResponse(
        val data: Data,
        val status: Int
    )

    data class Data(
        val exist: Boolean
    )
}