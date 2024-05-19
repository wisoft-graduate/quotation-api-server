package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.comment.CreateCommentUseCase
import wisoft.io.quotation.application.port.`in`.comment.DeleteCommentUseCase
import wisoft.io.quotation.application.port.`in`.comment.GetCommentListUseCase
import wisoft.io.quotation.application.port.`in`.comment.UpdateCommentUseCase
import wisoft.io.quotation.application.port.out.comment.*
import wisoft.io.quotation.application.port.out.quotation.GetQuotationPort
import wisoft.io.quotation.application.port.out.user.GetUserPort
import wisoft.io.quotation.domain.Comment
import wisoft.io.quotation.exception.error.CommentNotFoundException
import wisoft.io.quotation.exception.error.QuotationNotFoundException
import wisoft.io.quotation.exception.error.UserNotFoundException
import java.util.*

@Service
class CommentService(
    val createCommentPort: CreateCommentPort,
    val getUserPort: GetUserPort,
    val getQuotationPort: GetQuotationPort,
    val getCommentListPort: GetCommentListPort,
    val getCommentPort: GetCommentPort,
    val updateCommentPort: UpdateCommentPort,
    val deleteCommentPort: DeleteCommentPort,
) : CreateCommentUseCase,
    GetCommentListUseCase,
    UpdateCommentUseCase,
    DeleteCommentUseCase {
    val logger = KotlinLogging.logger {}

    override fun createComment(request: CreateCommentUseCase.CreateCommentRequest): UUID {
        return runCatching {
            // Validation Check
            getUserPort.getUserById(request.userId)
                ?: throw UserNotFoundException(request.userId)

            getQuotationPort.getQuotation(request.quotationId)
                ?: throw QuotationNotFoundException(request.quotationId.toString())

            request.commentedUserId?.let {
                getUserPort.getUserById(it)
                    ?: throw UserNotFoundException(it)
            }

            // Create Comment
            createCommentPort.createComment(
                Comment(
                    quotationId = request.quotationId,
                    userId = request.userId,
                    content = request.content,
                    commentedUserId = request.commentedUserId,
                    parentCommentId = request.parentCommentId,
                ),
            )
        }.onFailure {
            logger.error { "createComment fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getCommentList(request: GetCommentListUseCase.GetCommentListRequest): List<Comment> {
        return runCatching {
            getCommentListPort.getCommentList(
                request.commentIds,
                request.quotationId,
                request.parentId,
                request.isTopDepth,
            )
        }.onFailure {
            logger.error { "getCommentList fail: param[$request]" }
        }.getOrThrow()
    }

    override fun updateComment(
        id: UUID,
        request: UpdateCommentUseCase.UpdateCommentRequest,
    ): UUID {
        return runCatching {
            val comment = getCommentPort.getCommentById(id) ?: throw CommentNotFoundException(id.toString())
            request.commentedUserId?.let {
                getUserPort.getUserById(it) ?: throw UserNotFoundException(it)
            }

            updateCommentPort.update(comment.update(request))
        }.onFailure {
            logger.error { "updateComment fail: param[$request]" }
        }.getOrThrow()
    }

    override fun deleteComment(id: UUID) {
        return runCatching {
            getCommentPort.getCommentById(id) ?: throw CommentNotFoundException(id.toString())
            deleteCommentPort.deleteComment(id)
        }.onFailure {
            logger.error { "deleteComment fail: param[id: $id]" }
        }.getOrThrow()
    }
}
