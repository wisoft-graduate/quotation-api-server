package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.Quotation
import java.util.UUID

interface GetQuotationPort {

    fun getQuotation(id: UUID): Quotation
}