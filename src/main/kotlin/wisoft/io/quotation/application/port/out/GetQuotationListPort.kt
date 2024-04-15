package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.application.port.`in`.GetQuotationListUseCase
import wisoft.io.quotation.domain.Quotation

interface GetQuotationListPort {

    fun getQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<Quotation>

}