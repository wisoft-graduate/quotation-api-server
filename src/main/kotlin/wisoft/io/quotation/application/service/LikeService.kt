package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.like.CreateLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.DeleteLikeUseCase
import wisoft.io.quotation.application.port.out.like.CreateLikePort
import wisoft.io.quotation.application.port.out.like.DeleteLikePort
import wisoft.io.quotation.application.port.out.like.GetLikePort
import wisoft.io.quotation.application.port.out.quotation.GetQuotationPort
import wisoft.io.quotation.application.port.out.quotation.UpdateQuotationPort
import wisoft.io.quotation.application.port.out.user.GetUserPort
import wisoft.io.quotation.domain.Like
import wisoft.io.quotation.exception.error.LikeNotFoundException
import wisoft.io.quotation.exception.error.QuotationNotFoundException
import wisoft.io.quotation.exception.error.UserNotFoundException
import java.util.*

@Service
class LikeService(
    val createLikePort: CreateLikePort,
    val getUserPort: GetUserPort,
    val getQuotationPort: GetQuotationPort,
    val deleteLikePort: DeleteLikePort,
    val getLikePort: GetLikePort,
    val updateQuotationPort: UpdateQuotationPort,
) : CreateLikeUseCase,
    DeleteLikeUseCase {
    val logger = KotlinLogging.logger { }

    override fun createLike(request: CreateLikeUseCase.CreateLikeRequest): UUID {
        return runCatching {
            getUserPort.getUserById(request.userId) ?: throw UserNotFoundException(request.userId)
            getQuotationPort.getQuotation(request.quotationId)
                ?: throw QuotationNotFoundException(request.quotationId.toString())
            updateQuotationPort.incrementLikeCount(request.quotationId)
            createLikePort.createLike(Like(userId = request.userId, quotationId = request.quotationId))
        }.onFailure {
            logger.error { "createLike fail: param[$request]" }
        }.getOrThrow()
    }

    override fun deleteLike(id: UUID) {
        return runCatching {
            val like = getLikePort.getLikeById(id) ?: throw LikeNotFoundException(id.toString())
            updateQuotationPort.decrementLikeCount(like.quotationId)
            deleteLikePort.deleteLike(id)
        }.onFailure {
            logger.error { "deleteLike fail: param[id: $id]" }
        }.getOrThrow()
    }
}
