package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import wisoft.io.quotation.application.port.`in`.author.CreateAuthorUseCase
import wisoft.io.quotation.application.port.`in`.author.DeleteAuthorUseCase
import wisoft.io.quotation.application.port.`in`.author.UpdateAuthorUseCase
import java.util.*

@Controller()
class AuthorController(
    val createAuthorUseCase: CreateAuthorUseCase,
    val updateAuthorUseCase: UpdateAuthorUseCase,
    val deleteAuthorUseCase: DeleteAuthorUseCase,
) {
    @PostMapping("/authors")
    fun createAuthor(
        @Valid @RequestBody request: CreateAuthorUseCase.CreateAuthorRequest,
    ): ResponseEntity<CreateAuthorUseCase.CreateAuthorResponse> {
        val result = createAuthorUseCase.createAuthor(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CreateAuthorUseCase.CreateAuthorResponse(
                    CreateAuthorUseCase.Data(id = result),
                ),
            )
    }

    @PutMapping("/authors/{id}")
    fun updateAuthor(
        @PathVariable("id") id: UUID,
        @RequestBody request: UpdateAuthorUseCase.UpdateAuthorRequest,
    ): ResponseEntity<UpdateAuthorUseCase.UpdateAuthorResponse> {
        val result = updateAuthorUseCase.updateAuthor(id, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                UpdateAuthorUseCase.UpdateAuthorResponse(
                    UpdateAuthorUseCase.Data(id = result),
                ),
            )
    }

    @DeleteMapping("/authors/{id}")
    fun deleteAuthor(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<Void> {
        deleteAuthorUseCase.deleteAuthor(id)

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}
