package wisoft.io.quotation.application.port.`in`

interface ResignUseCase {

    fun resign(id: String): String

    data class ResignResponse(
        val data: Data,
        val status: Int
    )

    data class Data(
        val id: String
    )

}