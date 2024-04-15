package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank
import wisoft.io.quotation.domain.Bookmark

interface GetBookmarkListUseCase {

    fun getBookmarkList(request: GetBookmarkListRequest): List<Bookmark>

    data class GetBookmarkListRequest(
        @field:NotBlank
        val userId: String
    )

    data class GetBookmarkListResponse(
        val data: Data
    )

    data class Data(
        val bookmarks: List<Bookmark>
    )
}