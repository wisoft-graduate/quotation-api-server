package wisoft.io.quotation.application.port.out.like

import java.util.UUID

interface DeleteLikePort {
    fun deleteLike(id: UUID)
}
