package wisoft.io.quotation.adaptor.`in`.http

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import wisoft.io.quotation.application.port.`in`.like.CreateLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.DeleteLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.GetLikeListUseCase
import java.util.UUID

@RestController
@RequestMapping("/likes")
class LikeController(
    val createLikeUseCase: CreateLikeUseCase,
    val deleteLikeUseCase: DeleteLikeUseCase,
    val getLikeUseCase: GetLikeListUseCase,
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
    fun getLikeList(
        @PathVariable("userId") userId: String,
        @RequestParam("quotationId") quotationId: UUID?,
    ): ResponseEntity<GetLikeListUseCase.GetLikeListResponse> {
        getLikeUseCase.getLikeList(userId, quotationId)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                GetLikeListUseCase.GetLikeListResponse(
                    data = getLikeUseCase.getLikeList(userId, quotationId),
                ),
            )
    }
}
