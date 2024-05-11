package wisoft.io.quotation.application.port.`in`.author

import wisoft.io.quotation.domain.Author
import java.util.UUID

interface GetAuthorUseCase {
    fun getAuthor(id: UUID): Author

    data class GetAuthorResponse(
        val data: Author,
    )
}
