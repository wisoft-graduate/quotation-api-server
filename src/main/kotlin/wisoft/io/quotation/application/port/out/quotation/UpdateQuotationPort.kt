package wisoft.io.quotation.application.port.out.quotation

import java.util.*

interface UpdateQuotationPort {
    fun incrementLikeCount(id: UUID)

    fun decrementLikeCount(id: UUID)
}
