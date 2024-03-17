package wisoft.io.quotation.adaptor.`in`.http

import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.GetQuotationUseCase
import wisoft.io.quotation.application.port.`in`.GetQuotationsUseCase
import java.util.UUID

@RestController
@RequestMapping("/quotations")
class QuotationController(
    val getQuotationsUseCase: GetQuotationsUseCase,
    val getQuotationUseCase: GetQuotationUseCase
) {

    @GetMapping
    fun getQuotations(@ModelAttribute request: GetQuotationsUseCase.GetQuotationRequest):
            ResponseEntity<GetQuotationsUseCase.GetQuotationResponse> {
        val response = getQuotationsUseCase.getQuotations(request)
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationsUseCase.GetQuotationResponse(data = GetQuotationsUseCase.Data(quotations = response)))
    }

    @GetMapping("/{id}")
    fun getQuotation(@PathVariable("id") id: UUID): ResponseEntity<GetQuotationUseCase.GetQuotationResponse> {
        println("들어옴")
        val response = getQuotationUseCase.getQuotation(id)
        return ResponseEntity.status(HttpStatus.OK)
            .body(GetQuotationUseCase.GetQuotationResponse(data = GetQuotationUseCase.Data(response)))
    }
}