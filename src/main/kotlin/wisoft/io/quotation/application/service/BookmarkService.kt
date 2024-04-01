package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.CreateBookmarkUseCase
import wisoft.io.quotation.application.port.out.CreateBookmarkPort
import wisoft.io.quotation.application.port.out.GetUserByIdPort
import wisoft.io.quotation.domain.Bookmark
import wisoft.io.quotation.exception.error.UserNotFoundException
import java.util.*

@Service
class BookmarkService(
    val getUserByIdPort: GetUserByIdPort,
    val createBookmarkPort: CreateBookmarkPort
    ) : CreateBookmarkUseCase {
    val logger = KotlinLogging.logger {}

    override fun createBookmark(request: CreateBookmarkUseCase.CreateBookmarkRequest): UUID {
        return runCatching {
            getUserByIdPort.getByIdOrNull(request.userId) ?: throw UserNotFoundException(request.userId)
            request.run {
                createBookmarkPort.createBookmark(
                    Bookmark(
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

}