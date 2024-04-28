package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.CreateCommentUseCase

@RestController
class CommentController(
    val createCommentUseCase: CreateCommentUseCase,
) {
    @PostMapping("/comments")
    fun createComment(
        @Valid @RequestBody request: CreateCommentUseCase.CreateCommentRequest,
    ): ResponseEntity<CreateCommentUseCase.CreateCommentResponse> {
        val result = createCommentUseCase.createComment(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CreateCommentUseCase.CreateCommentResponse(
                    CreateCommentUseCase.Data(id = result),
                ),
            )
    }
}
