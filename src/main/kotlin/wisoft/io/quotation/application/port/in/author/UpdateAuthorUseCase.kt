package wisoft.io.quotation.application.port.`in`.author

import java.util.*

interface UpdateAuthorUseCase {
    fun updateAuthor(
        id: UUID,
        request: UpdateAuthorRequest,
    ): UUID

    data class UpdateAuthorRequest(
        val name: String?,
        val countryCode: String?,
    )

    data class UpdateAuthorResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
