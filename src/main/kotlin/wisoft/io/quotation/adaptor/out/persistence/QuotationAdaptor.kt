package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.application.port.out.GetQuotationsPort
import wisoft.io.quotation.domain.Quotation

@Component
class QuotationAdaptor(
    val quotationRepository: QuotationRepository,
    val quotationCustomRepository: QuotationCustomRepository
) : GetQuotationsPort {


    override fun getQuotations(request: GetQuotationsUseCase.GetQuotationRequest): List<Quotation> {
        val result = quotationCustomRepository.findQuotations(request)
        return result.map { it.toDomain() }
    }

}