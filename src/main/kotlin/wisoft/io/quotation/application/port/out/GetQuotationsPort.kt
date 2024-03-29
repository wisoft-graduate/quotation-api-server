package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.domain.Quotation

interface GetQuotationsPort {

    fun getQuotationList(request: GetQuotationsUseCase.GetQuotationListRequest): List<Quotation>

}