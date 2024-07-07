package wisoft.io.quotation.application.port.out.like

import wisoft.io.quotation.domain.Like
import java.util.UUID

interface CreateLikePort {
    fun createLike(like: Like): UUID
}
