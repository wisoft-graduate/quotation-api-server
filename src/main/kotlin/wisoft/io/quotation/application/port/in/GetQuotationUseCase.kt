package wisoft.io.quotation.application.port.`in`

import wisoft.io.quotation.domain.Quotation
import java.util.UUID

interface GetQuotationUseCase {

    fun getQuotation(id: UUID): Quotation

    data class GetQuotationResponse(
        val data: Data
    )

    data class Data(
        val quotation: Quotation
    )
}