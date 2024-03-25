package wisoft.io.quotation.adaptor.`in`.http

import jakarta.servlet.http.HttpServletRequest
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
import wisoft.io.quotation.application.port.`in`.GetExistUserUseCase
import wisoft.io.quotation.application.port.`in`.DeleteUserUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.CreateUserUseCase

@RestController
class UserController(
    val createUserUseCase: CreateUserUseCase,
    val signInUseCase: SignInUseCase,
    val deleteUserUseCase: DeleteUserUseCase,
    val getExistUserUseCase: GetExistUserUseCase
) {

    @PostMapping("/users")
    fun createUser(
        servletRequest: HttpServletRequest,
        @RequestBody @Valid request: CreateUserUseCase.CreateUserRequest
    ): ResponseEntity<CreateUserUseCase.CreateUserResponse> {
        val response = createUserUseCase.createUser(request)
        val responseUrl = servletRequest.requestURL.append("/").append(response).toString()
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                CreateUserUseCase.CreateUserResponse(
                    data = CreateUserUseCase.Data(location = responseUrl),
                )
            )
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
                    ),
                )
            )
    }

    @GetMapping("/users/exist")
    fun getExistUser(
        @RequestParam("id") id: String,
        @RequestParam("nickname") nickname: String
    ): ResponseEntity<GetExistUserUseCase.GetExistUserResponse> {
        val response = getExistUserUseCase.getExistUser(id, nickname)
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                GetExistUserUseCase.GetExistUserResponse(
                    data = GetExistUserUseCase.Data(exist = response),
                )
            )
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable("id") id: String): ResponseEntity<DeleteUserUseCase.DeleteUserResponse> {
        deleteUserUseCase.deleteUser(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }



}