package wisoft.io.quotation.application.port.out.quotation

import java.util.*

interface UpdateQuotationPort {
    fun incrementComment(id: UUID)

    fun decrementComment(id: UUID)
}
