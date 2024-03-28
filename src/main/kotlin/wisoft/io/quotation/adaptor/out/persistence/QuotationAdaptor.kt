package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.application.port.out.GetQuotationPort
import wisoft.io.quotation.application.port.out.GetQuotationsPort
import wisoft.io.quotation.domain.Quotation
import java.util.*

@Component
class QuotationAdaptor(
    val quotationRepository: QuotationRepository,
    val quotationCustomRepository: QuotationCustomRepository
) : GetQuotationsPort,
    GetQuotationPort {

    override fun getQuotation(id: UUID): Quotation? {
        return quotationRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun getQuotationList(request: GetQuotationsUseCase.GetQuotationListRequest): List<Quotation> {
        val result = quotationCustomRepository.findQuotationList(request).distinct()
        return result.map { it.toDomain() }
    }

}