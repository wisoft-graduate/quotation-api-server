package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.ExistUserUseCase
import wisoft.io.quotation.application.port.`in`.ResignUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.SignUpUseCase

@RestController
class UserController(
    val signUpUseCase: SignUpUseCase,
    val signInUseCase: SignInUseCase,
    val resignUseCase: ResignUseCase,
    val existUserUseCase: ExistUserUseCase
) {

    @PostMapping("/users")
    fun signUp(@RequestBody @Valid request: SignUpUseCase.SignUpRequest): ResponseEntity<SignUpUseCase.SignUpResponse> {
        val response = signUpUseCase.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(SignUpUseCase.SignUpResponse(data = SignUpUseCase.Data(id = response)))
    }

    @PostMapping("/users/sign-in")
    fun signIn(@RequestBody @Valid request: SignInUseCase.SignInRequest): ResponseEntity<SignInUseCase.SignInResponse> {
        val response = signInUseCase.signIn(request)
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                SignInUseCase.SignInResponse(
                    data = SignInUseCase.UserTokenDto(
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                )
            )
    }

    @GetMapping("/users/exist")
    fun exist(
        @RequestParam("id") id: String,
        @RequestParam("nickname") nickname: String
    ): ResponseEntity<ExistUserUseCase.ExistUserResponse> {
        val response = existUserUseCase.existUser(id, nickname)
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                ExistUserUseCase.ExistUserResponse(
                    data = ExistUserUseCase.Data(exist = response)
                )
            )
    }

    @DeleteMapping("/users/{id}")
    fun resign(@PathVariable("id") id: String): ResponseEntity<ResignUseCase.ResignResponse> {
        val response = resignUseCase.resign(id)
        // FIXME : NO_CONTENT 는 본문 자체가 없어야 함.
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(
                ResignUseCase.ResignResponse(
                    data = ResignUseCase.Data(id = response)
                )
            )
    }


}