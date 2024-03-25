package wisoft.io.quotation.application.port.`in`

interface DeleteUserUseCase {

    fun deleteUser(id: String): String

    data class DeleteUserResponse(
        val data: Data,
    )

    data class Data(
        val id: String
    )

}