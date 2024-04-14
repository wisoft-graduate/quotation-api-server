package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.CreateBookmarkUseCase
import wisoft.io.quotation.application.port.`in`.GetBookmarkListUseCase

@RestController
class BookmarkController(
    val createBookmarkUseCase: CreateBookmarkUseCase,
    val getBookmarkListUseCase: GetBookmarkListUseCase
) {

    @PostMapping("/bookmark")
    fun createBookmark(@Valid @RequestBody request: CreateBookmarkUseCase.CreateBookmarkRequest):
            ResponseEntity<CreateBookmarkUseCase.CreateBookmarkResponse> {
        val result = createBookmarkUseCase.createBookmark(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CreateBookmarkUseCase.CreateBookmarkResponse(
                    CreateBookmarkUseCase.Data(id = result)
                )
            )
    }

    @GetMapping("/bookmark")
    fun getBookmarkList(@Valid @RequestParam userId: String):
            ResponseEntity<GetBookmarkListUseCase.GetBookmarkListResponse> {
        val result = getBookmarkListUseCase.getBookmarkList(
            GetBookmarkListUseCase.GetBookmarkListRequest(userId = userId)
        )
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                GetBookmarkListUseCase.GetBookmarkListResponse(
                    GetBookmarkListUseCase.Data(bookmarks = result)
                )
            )

    }
}