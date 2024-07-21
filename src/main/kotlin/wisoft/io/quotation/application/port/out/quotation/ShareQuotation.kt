package wisoft.io.quotation.application.port.out.quotation

import java.util.UUID

interface ShareQuotationPort {
    fun shareQuotation(id: UUID)
}
