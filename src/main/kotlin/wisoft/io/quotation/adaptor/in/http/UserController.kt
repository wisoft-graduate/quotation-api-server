package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.GetUserListUseCase
import wisoft.io.quotation.application.port.`in`.DeleteUserUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.CreateUserUseCase

@RestController
class UserController(
    val createUserUseCase: CreateUserUseCase,
    val signInUseCase: SignInUseCase,
    val deleteUserUseCase: DeleteUserUseCase,
    val getUserListUseCase: GetUserListUseCase
) {

    @PostMapping("/users")
    fun createUser(
        @RequestBody @Valid request: CreateUserUseCase.CreateUserRequest
    ): ResponseEntity<CreateUserUseCase.CreateUserResponse> {
        val response = createUserUseCase.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                CreateUserUseCase.CreateUserResponse(
                    data = CreateUserUseCase.Data(id = response),
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

    @GetMapping("/users")
    fun getUserList(
        @ModelAttribute request: GetUserListUseCase.GetUserListRequest
    ): ResponseEntity<GetUserListUseCase.GetUserListResponse> {
        val response = getUserListUseCase.getUserList(request)
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                GetUserListUseCase.GetUserListResponse(
                    data = response,
                )
            )
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable("id") id: String): ResponseEntity<DeleteUserUseCase> {
        deleteUserUseCase.deleteUser(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }


}