package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.application.port.out.GetQuotationsPort
import wisoft.io.quotation.domain.Quotation

@Service
class QuotationService(val getQuotationsPort: GetQuotationsPort): GetQuotationsUseCase {

    override fun getQuotations(request: GetQuotationsUseCase.GetQuotationRequest): List<Quotation> {
        return getQuotationsPort.getQuotations(request)
    }
}