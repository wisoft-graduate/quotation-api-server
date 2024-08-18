package wisoft.io.quotation.application.port.out.quotation

import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationRankView
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationRankUseCase
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.domain.QuotationView
import java.util.*

interface GetQuotationListPort {
    fun getQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<QuotationView>

    fun getQuotationListByIds(ids: List<UUID>): List<Quotation>

    fun getQuotationRank(request: GetQuotationRankUseCase.GetQuotationRankRequest): List<QuotationRankView>
}
