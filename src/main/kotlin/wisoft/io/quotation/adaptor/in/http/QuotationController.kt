package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationRankUseCase
import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationUseCase
import wisoft.io.quotation.domain.Paging
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.SortDirection
import java.util.UUID

@RestController
@RequestMapping("/quotations")
class QuotationController(
    val getQuotationsUseCase: GetQuotationListUseCase,
    val getQuotationUseCase: GetQuotationUseCase,
    val getQuotationLankUseCase: GetQuotationRankUseCase,
) {
    @GetMapping("/rank")
    fun getQuotationRank(
        @RequestParam ids: List<UUID>?,
    ): ResponseEntity<GetQuotationRankUseCase.GetQuotationRankResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(
            GetQuotationRankUseCase.GetQuotationRankResponse(
                data = GetQuotationRankUseCase.Data(getQuotationLankUseCase.getQuotationRank(ids)),
            ),
        )
    }

    @GetMapping
    fun getQuotationList(
        @RequestParam searchWord: String?,
        @RequestParam sortTarget: QuotationSortTarget?,
        @RequestParam sortDirection: SortDirection?,
        @ModelAttribute paging: Paging?,
        @RequestParam ids: List<UUID>?,
    ): ResponseEntity<GetQuotationListUseCase.GetQuotationListResponse> {
        val response =
            getQuotationsUseCase.getQuotationList(
                GetQuotationListUseCase.GetQuotationListRequest(
                    searchWord = searchWord,
                    sortTarget = sortTarget,
                    sortDirection = sortDirection,
                    paging = paging,
                    ids = ids,
                ),
            )
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationListUseCase.GetQuotationListResponse(data = GetQuotationListUseCase.Data(quotationList = response)))
    }

    @GetMapping("/{id}")
    fun getQuotation(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<GetQuotationUseCase.GetQuotationResponse> {
        val response = getQuotationUseCase.getQuotation(id)
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationUseCase.GetQuotationResponse(data = response))
    }
}
