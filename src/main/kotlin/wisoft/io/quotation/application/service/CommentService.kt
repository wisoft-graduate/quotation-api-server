package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.CreateCommentUseCase
import wisoft.io.quotation.application.port.out.CreateCommentPort
import wisoft.io.quotation.application.port.out.GetQuotationPort
import wisoft.io.quotation.application.port.out.GetUserPort
import wisoft.io.quotation.domain.Comment
import wisoft.io.quotation.exception.error.QuotationNotFoundException
import wisoft.io.quotation.exception.error.UserNotFoundException
import java.util.*

@Service
class CommentService(
    val createCommentPort: CreateCommentPort,
    val getUserPort: GetUserPort,
    val getQuotationPort: GetQuotationPort,
) : CreateCommentUseCase {
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
}
