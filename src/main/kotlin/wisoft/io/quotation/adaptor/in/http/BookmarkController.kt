package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.bookmark.CreateBookmarkUseCase
import wisoft.io.quotation.application.port.`in`.bookmark.DeleteBookmarkUseCase
import wisoft.io.quotation.application.port.`in`.bookmark.GetBookmarkListUseCase
import wisoft.io.quotation.application.port.`in`.bookmark.UpdateBookmarkUseCase
import java.util.UUID

@RestController
class BookmarkController(
    val createBookmarkUseCase: CreateBookmarkUseCase,
    val getBookmarkListUseCase: GetBookmarkListUseCase,
    val updateBookmarkUseCase: UpdateBookmarkUseCase,
    val deleteBookmarkUseCase: DeleteBookmarkUseCase,
) {
    @PostMapping("/bookmark")
    fun createBookmark(
        @Valid @RequestBody request: CreateBookmarkUseCase.CreateBookmarkRequest,
    ): ResponseEntity<CreateBookmarkUseCase.CreateBookmarkResponse> {
        val result = createBookmarkUseCase.createBookmark(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CreateBookmarkUseCase.CreateBookmarkResponse(
                    CreateBookmarkUseCase.Data(id = result),
                ),
            )
    }

    @GetMapping("/bookmark")
    fun getBookmarkList(
        @Valid @RequestParam userId: String,
    ): ResponseEntity<GetBookmarkListUseCase.GetBookmarkListResponse> {
        val result =
            getBookmarkListUseCase.getBookmarkList(
                GetBookmarkListUseCase.GetBookmarkListRequest(userId = userId),
            )
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                GetBookmarkListUseCase.GetBookmarkListResponse(data = result),
            )
    }

    @PutMapping("/bookmark/{id}")
    fun updateBookmark(
        @PathVariable("id") id: UUID,
        @RequestBody request: UpdateBookmarkUseCase.UpdateBookmarkRequest,
    ): ResponseEntity<UpdateBookmarkUseCase.UpdateBookmarkResponse> {
        val result = updateBookmarkUseCase.updateBookmark(id, request)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                UpdateBookmarkUseCase.UpdateBookmarkResponse(
                    UpdateBookmarkUseCase.Data(id = result),
                ),
            )
    }

    @DeleteMapping("/bookmark/{id}")
    fun deleteBookmark(
        @PathVariable("id") id: UUID,
    ): ResponseEntity<Void> {
        deleteBookmarkUseCase.deleteBookmark(id)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }
}
