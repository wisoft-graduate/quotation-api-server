package wisoft.io.quotation.application.service

import mu.KotlinLogging
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

    val logger = KotlinLogging.logger {}
    override fun getQuotationList(request: GetQuotationsUseCase.GetQuotationListRequest): List<Quotation> {
        return runCatching {
            getQuotationsPort.getQuotationList(request)
        }.onFailure {
            logger.error { "getQuotationList fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getQuotation(id: UUID): Quotation {
        return runCatching {
            // TODO ExceptionHandler pr 이후 다른 티켓에서 작업
            getQuotationPort.getQuotation(id) ?: throw RuntimeException()
        }.onFailure {
            logger.error { "getQuotation fail: param[id: $id]" }
        }.getOrThrow()
    }
}