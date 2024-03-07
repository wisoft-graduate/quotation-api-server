package wisoft.io.quotation.application.port.`in`

interface ResignUseCase {

    fun resign(id: String): String

    data class ResignResponse(
        val data: Data,
    )

    data class Data(
        val id: String
    )

}