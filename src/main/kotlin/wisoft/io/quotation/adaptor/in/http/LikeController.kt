package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.like.CreateLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.DeleteLikeUseCase
import java.util.UUID

@RestController
@RequestMapping("/likes")
class LikeController(val createLikeUseCase: CreateLikeUseCase, val deleteLikeUseCase: DeleteLikeUseCase) {
    @PostMapping
    fun createLike(
        @RequestBody request: CreateLikeUseCase.CreateLikeRequest,
    ): ResponseEntity<CreateLikeUseCase.CreateLikeResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CreateLikeUseCase.CreateLikeResponse(
                    data = CreateLikeUseCase.Data(createLikeUseCase.createLike(request)),
                ),
            )
    }

    @DeleteMapping("/{id}")
    fun deleteLike(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<Unit> {
        deleteLikeUseCase.deleteLike(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}
