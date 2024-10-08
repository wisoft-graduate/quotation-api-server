package wisoft.io.quotation.application.service

import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationRankView
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationRankUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationUseCase
import wisoft.io.quotation.application.port.`in`.quotation.ShareQuotationUseCase
import wisoft.io.quotation.application.port.out.quotation.GetQuotationListPort
import wisoft.io.quotation.application.port.out.quotation.GetQuotationPort
import wisoft.io.quotation.application.port.out.quotation.ShareQuotationPort
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.domain.QuotationView
import wisoft.io.quotation.exception.error.QuotationNotFoundException
import java.util.*

@Service
class QuotationService(
    val getQuotationsPort: GetQuotationListPort,
    val getQuotationPort: GetQuotationPort,
    val shareQuotationPort: ShareQuotationPort,
) : GetQuotationListUseCase,
    GetQuotationRankUseCase,
    GetQuotationUseCase,
    ShareQuotationUseCase {
    val logger = KotlinLogging.logger {}

    override fun getQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<QuotationView> {
        return runCatching {
            getQuotationsPort.getQuotationList(request)
        }.onFailure {
            logger.error { "getQuotationList fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getQuotationRank(request: GetQuotationRankUseCase.GetQuotationRankRequest): List<QuotationRankView> {
        return runCatching {
            getQuotationsPort.getQuotationRank(request)
        }.onFailure {
            logger.error { "getQuotationRank fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getQuotation(id: UUID): Quotation {
        return runCatching {
            getQuotationPort.getQuotation(id)
                ?: throw QuotationNotFoundException(id.toString())
        }.onFailure {
            logger.error { "getQuotation fail: param[id: $id]" }
        }.getOrThrow()
    }

    @Transactional
    override fun shareQuotation(id: UUID) {
        return runCatching {
            getQuotationPort.getQuotation(id)
                ?: throw QuotationNotFoundException(id.toString())
            shareQuotationPort.shareQuotation(id)
        }.onFailure {
            logger.error { "shareQuotation fail: param[$id]" }
        }.getOrThrow()
    }
}
