package wisoft.io.quotation.application.port.`in`.like

import java.util.UUID

interface DeleteLikeUseCase {
    fun deleteLike(id: UUID)
}
