package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationUseCase
import wisoft.io.quotation.application.port.out.quotation.GetQuotationListPort
import wisoft.io.quotation.application.port.out.quotation.GetQuotationPort
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.exception.error.QuotationNotFoundException
import java.util.*

@Service
class QuotationService(
    val getQuotationsPort: GetQuotationListPort,
    val getQuotationPort: GetQuotationPort,
) : GetQuotationListUseCase,
    GetQuotationUseCase {
    val logger = KotlinLogging.logger {}

    override fun getQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<Quotation> {
        return runCatching {
            getQuotationsPort.getQuotationList(request)
        }.onFailure {
            logger.error { "getQuotationList fail: param[$request]" }
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
}
