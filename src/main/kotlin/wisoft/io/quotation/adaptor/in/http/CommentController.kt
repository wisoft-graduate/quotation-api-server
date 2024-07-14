package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wisoft.io.quotation.application.port.`in`.comment.CreateCommentUseCase
import wisoft.io.quotation.application.port.`in`.comment.DeleteCommentUseCase
import wisoft.io.quotation.application.port.`in`.comment.GetCommentListUseCase
import wisoft.io.quotation.application.port.`in`.comment.UpdateCommentUseCase
import java.util.UUID

@RestController
class CommentController(
    val createCommentUseCase: CreateCommentUseCase,
    val getCommentListUseCase: GetCommentListUseCase,
    val updateCommentUseCase: UpdateCommentUseCase,
    val deleteCommentUseCase: DeleteCommentUseCase,
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

    @GetMapping("/comments")
    fun getCommentList(
        @ModelAttribute request: GetCommentListUseCase.GetCommentListRequest,
    ): ResponseEntity<GetCommentListUseCase.GetCommentListResponse> {
        val result = getCommentListUseCase.getCommentList(request)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                GetCommentListUseCase.GetCommentListResponse(
                    data = result,
                ),
            )
    }

    @PutMapping("/comments/{id}")
    fun updateComment(
        @PathVariable("id") id: UUID,
        @RequestBody request: UpdateCommentUseCase.UpdateCommentRequest,
    ): ResponseEntity<UpdateCommentUseCase.UpdateCommentResponse> {
        val result = updateCommentUseCase.updateComment(id, request)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(UpdateCommentUseCase.UpdateCommentResponse(data = UpdateCommentUseCase.Data(id = result)))
    }

    @DeleteMapping("/comments/{id}")
    fun deleteComment(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<Unit> {
        deleteCommentUseCase.deleteComment(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}
