package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wisoft.io.quotation.application.port.`in`.like.CreateLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.DeleteLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.GetLikeUseCase
import java.util.UUID

@RestController
@RequestMapping("/likes")
class LikeController(
    val createLikeUseCase: CreateLikeUseCase,
    val deleteLikeUseCase: DeleteLikeUseCase,
    val getLikeUseCase: GetLikeUseCase,
) {
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

    @GetMapping("/{userId}")
    fun getLike(
        @PathVariable("userId") userId: String,
        @RequestParam("quotationId") quotationId: UUID,
    ): ResponseEntity<GetLikeUseCase.GetLikeResponse> {
        getLikeUseCase.getLike(userId, quotationId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                GetLikeUseCase.GetLikeResponse(
                    data = getLikeUseCase.getLike(userId, quotationId),
                ),
            )
    }
}
