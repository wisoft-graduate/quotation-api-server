package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.CommentEntity
import java.util.UUID

fun getCommentEntityFixture(
    quotationId: UUID,
    userId: String,
    parentId: UUID? = null,
    commentedUserId: String? = null,
): CommentEntity =
    CommentEntity(
        quotationId = quotationId,
        userId = userId,
        content = "content",
        parentId = parentId,
        commentedUserId = commentedUserId,
    )
