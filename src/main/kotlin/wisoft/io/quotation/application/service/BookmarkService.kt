package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.CreateBookmarkUseCase
import wisoft.io.quotation.application.port.`in`.DeleteBookmarkUseCase
import wisoft.io.quotation.application.port.`in`.GetBookmarkListUseCase
import wisoft.io.quotation.application.port.`in`.UpdateBookmarkUseCase
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.Bookmark
import wisoft.io.quotation.exception.error.BookmarkNotFoundException
import wisoft.io.quotation.exception.error.UserNotFoundException
import java.util.*

@Service
class BookmarkService(
    val getUserByIdPort: GetUserByIdPort,
    val createBookmarkPort: CreateBookmarkPort,
    val getBookmarkListPort: GetBookmarkListPort,
    val getBookmarkPort: GetBookmarkPort,
    val updateBookmarkPort: UpdateBookmarkPort,
    val deleteBookmarkPort: DeleteBookmarkPort
) : CreateBookmarkUseCase,
    GetBookmarkListUseCase,
    UpdateBookmarkUseCase,
    DeleteBookmarkUseCase {
    val logger = KotlinLogging.logger {}

    override fun createBookmark(request: CreateBookmarkUseCase.CreateBookmarkRequest): UUID {
        return runCatching {
            getUserByIdPort.getByIdOrNull(request.userId) ?: throw UserNotFoundException(request.userId)
            request.run {
                createBookmarkPort.createBookmark(
                    Bookmark(
                        id = this.id ?: UUID.randomUUID(),
                        name = this.name,
                        userId = this.userId,
                        visibility = this.visibility,
                        icon = this.icon,
                    )
                )
            }
        }.onFailure {
            logger.error { "createBookmark fail: param[$request]" }
        }.getOrThrow()
    }

    override fun updateBookmark(id: UUID, request: UpdateBookmarkUseCase.UpdateBookmarkRequest): UUID {
        return runCatching {
            val bookmark = getBookmarkPort.getBookmark(id) ?: throw BookmarkNotFoundException(id.toString())
            updateBookmarkPort.updateBookmark(bookmark.update(request))
        }.onFailure {
            logger.error { "updateBookmark fail: param[$request]" }
        }.getOrThrow()
    }

    override fun deleteBookmark(id: UUID) {
        runCatching {
            val bookmark = getBookmarkPort.getBookmark(id) ?: throw BookmarkNotFoundException(id.toString())
            deleteBookmarkPort.deleteBookmark(id)
        }.onFailure {
            logger.error { "deleteBookmark fail: param[id: $id]" }
        }.getOrThrow()
    }

    override fun getBookmarkList(request: GetBookmarkListUseCase.GetBookmarkListRequest): List<Bookmark> {
        return runCatching {
            getBookmarkListPort.getBookmarkList(request.userId)
        }.onFailure {
            logger.error { "getBookmarkList fail: param[$request]" }
        }.getOrThrow()
    }

}