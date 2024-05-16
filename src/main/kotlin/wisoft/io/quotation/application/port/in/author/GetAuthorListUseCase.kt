package wisoft.io.quotation.application.port.`in`.author

import wisoft.io.quotation.domain.Author

interface GetAuthorListUseCase {
    fun getAuthorList(): List<Author>

    data class GetAuthorListResponse(
        val data: List<Author>,
    )
}
