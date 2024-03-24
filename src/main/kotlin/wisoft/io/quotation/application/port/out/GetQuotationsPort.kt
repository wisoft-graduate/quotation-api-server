package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.domain.Quotation

interface GetQuotationsPort {

    fun getQuotations(request: GetQuotationsUseCase.GetQuotationsRequest): List<Quotation>

}