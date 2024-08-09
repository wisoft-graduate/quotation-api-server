package wisoft.io.quotation.application.service

import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.like.CreateLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.DeleteLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.GetLikeListUseCase
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
    DeleteLikeUseCase,
    GetLikeListUseCase {
    val logger = KotlinLogging.logger { }

    @Transactional
    override fun createLike(request: CreateLikeUseCase.CreateLikeRequest): UUID {
        return runCatching {
            getUserPort.getUserById(request.userId) ?: throw UserNotFoundException(request.userId)
            val quotation =
                getQuotationPort.getQuotation(request.quotationId)
                    ?: throw QuotationNotFoundException(request.quotationId.toString())
            updateQuotationPort.incrementLikeCount(request.quotationId)
            createLikePort.createLike(Like(userId = request.userId, quotation = quotation))
        }.onFailure {
            logger.error { "createLike fail: param[$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun deleteLike(id: UUID) {
        return runCatching {
            val like = getLikePort.getLikeById(id) ?: throw LikeNotFoundException(id.toString())
            updateQuotationPort.decrementLikeCount(like.quotation.id)
            deleteLikePort.deleteLike(id)
        }.onFailure {
            logger.error { "deleteLike fail: param[id: $id]" }
        }.getOrThrow()
    }

    override fun getLikeList(
        userId: String,
        quotationId: UUID?,
    ): List<Like> {
        return runCatching {
            getUserPort.getUserById(userId) ?: throw UserNotFoundException(userId)
            if (quotationId != null) {
                getQuotationPort.getQuotation(quotationId) ?: throw QuotationNotFoundException(userId)
                getLikePort.getLikeByUserIdAndQuotationId(userId, quotationId)?.let { listOf(it) } ?: emptyList()
            } else {
                getLikePort.getLikeByUserId(userId)
            }
        }.onFailure {
            logger.error { "getLike fail: param[userId: $userId, quotationId: $quotationId]" }
        }.getOrThrow()
    }
}
