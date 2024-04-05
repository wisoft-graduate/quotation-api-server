package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.CreateBookmarkUseCase

@RestController
class BookmarkController(val createBookmarkUseCase: CreateBookmarkUseCase) {

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
}