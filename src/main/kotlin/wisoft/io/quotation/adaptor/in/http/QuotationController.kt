package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wisoft.io.quotation.application.port.`in`.GetQuotationUseCase
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import wisoft.io.quotation.domain.Paging
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.SortDirection
import java.util.UUID

@RestController
@RequestMapping("/quotations")
class QuotationController(
    val getQuotationsUseCase: GetQuotationsUseCase,
    val getQuotationUseCase: GetQuotationUseCase
) {

    @GetMapping
    fun getQuotations(
        @RequestParam searchWord: String?,
        @RequestParam sortTarget: QuotationSortTarget?,
        @RequestParam sortDirection: SortDirection?,
        @ModelAttribute paging: Paging?,
        @RequestParam ids: List<UUID>?
    ): ResponseEntity<GetQuotationsUseCase.GetQuotationsResponse> {
        val response = getQuotationsUseCase.getQuotations(
            GetQuotationsUseCase.GetQuotationsRequest(
                searchWord = searchWord,
                sortTarget = sortTarget,
                sortDirection = sortDirection,
                paging = paging,
                ids = ids
            )
        )
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationsUseCase.GetQuotationsResponse(data = GetQuotationsUseCase.Data(quotations = response)))
    }

    @GetMapping("/{id}")
    fun getQuotation(@PathVariable("id") id: UUID): ResponseEntity<GetQuotationUseCase.GetQuotationResponse> {
        val response = getQuotationUseCase.getQuotation(id)
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationUseCase.GetQuotationResponse(data = response))
    }
}