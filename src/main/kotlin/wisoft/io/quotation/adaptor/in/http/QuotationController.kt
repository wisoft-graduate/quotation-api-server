package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase

@RestController
@RequestMapping("/quotations")
class QuotationController(val getQuotationsUseCase: GetQuotationsUseCase) {

    @GetMapping
    fun getQuotations(@ModelAttribute request: GetQuotationsUseCase.GetQuotationRequest):
            ResponseEntity<GetQuotationsUseCase.GetQuotationResponse> {
        val response = getQuotationsUseCase.getQuotations(request)
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationsUseCase.GetQuotationResponse(data = GetQuotationsUseCase.Data(quotations = response)))
    }
}