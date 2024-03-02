package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.SignUpUseCase

@RestController
class UserController(val signUpUseCase: SignUpUseCase) {

    @PostMapping("/users")
    fun signUp(@RequestBody @Valid request: SignUpUseCase.SignUpRequest): ResponseEntity<SignUpUseCase.SignUpResponse> {
        val response = signUpUseCase.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(SignUpUseCase.SignUpResponse(id = response))
    }

}