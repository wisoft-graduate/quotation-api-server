package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.GetQuotationUseCase
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.application.port.out.GetQuotationPort
import wisoft.io.quotation.application.port.out.GetQuotationsPort
import wisoft.io.quotation.domain.Quotation
import java.util.*

@Service
class QuotationService(
    val getQuotationsPort: GetQuotationsPort,
    val getQuotationPort: GetQuotationPort
) : GetQuotationsUseCase,
    GetQuotationUseCase {

    override fun getQuotations(request: GetQuotationsUseCase.GetQuotationRequest): List<Quotation> {
        return getQuotationsPort.getQuotations(request)
    }

    override fun getQuotation(id: UUID): Quotation {
        return getQuotationPort.getQuotation(id)
    }
}