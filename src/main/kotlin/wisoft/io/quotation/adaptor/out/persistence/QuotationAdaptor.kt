package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationRankView
import wisoft.io.quotation.adaptor.out.persistence.mapeer.QuotationMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationRankUseCase
import wisoft.io.quotation.application.port.out.quotation.GetQuotationListPort
import wisoft.io.quotation.application.port.out.quotation.GetQuotationPort
import wisoft.io.quotation.domain.Quotation
import java.util.*

@Component
class QuotationAdaptor(
    val quotationRepository: QuotationRepository,
    val quotationCustomRepository: QuotationCustomRepository,
    val quotationEntityMapper: QuotationMapper,
) : GetQuotationListPort,
    GetQuotationPort {
    override fun getQuotation(id: UUID): Quotation? {
        return quotationRepository.findByIdOrNull(id)?.let {
            quotationEntityMapper.toDomain(entity = it)
        }
    }

    override fun getQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<Quotation> {
        val result = quotationCustomRepository.findQuotationList(request).distinct()
        return quotationEntityMapper.toDomains(result)
    }

    override fun getQuotationLank(request: GetQuotationRankUseCase.GetQuotationRankRequest): List<QuotationRankView> {
        return quotationCustomRepository.findQuotationRank(request)
    }
}
