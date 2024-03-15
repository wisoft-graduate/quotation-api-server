package wisoft.io.quotation.application.port.`in`

interface ExistUserUseCase {

    fun existUser(id: String, nickname: String): Boolean

    data class ExistUserResponse(
        val data: Data
    )

    data class Data(
        val exist: Boolean
    )
}