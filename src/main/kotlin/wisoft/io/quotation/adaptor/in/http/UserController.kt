package wisoft.io.quotation.adaptor.`in`.http

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import wisoft.io.quotation.application.port.`in`.*
import wisoft.io.quotation.util.annotation.Authenticated

@RestController
class UserController(
    val createUserUseCase: CreateUserUseCase,
    val signInUseCase: SignInUseCase,
    val deleteUserUseCase: DeleteUserUseCase,
    val getUserUseCase: GetUserUseCase,
    val getUserDetailUseCase: GetUserDetailUseCase,
    val getUserListUseCase: GetUserListUseCase,
    val updateUserUseCase: UpdateUserUseCase,
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

    @GetMapping("/users")
    fun getUserList(
        @RequestParam nickname: String
    ): ResponseEntity<GetUserListUseCase.GetUserListResponse> {
        val response = getUserListUseCase.getUserList(GetUserListUseCase.GetUserListRequest(nickname = nickname))

        return ResponseEntity.status(HttpStatus.OK)
            .body(
                GetUserListUseCase.GetUserListResponse(data = GetUserListUseCase.Data(users = response))
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

    @GetMapping("/users/duplication-check")
    fun getUser(
        @ModelAttribute request: GetUserUseCase.GetUserByIdOrNicknameRequest
    ): ResponseEntity<GetUserUseCase.GetUserByIdOrNicknameResponse> {
        val response = getUserUseCase.getUserByIdOrNickname(request)
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                GetUserUseCase.GetUserByIdOrNicknameResponse(
                    data = GetUserUseCase.UserDto(id = response.id, nickname = response.nickname),
                )
            )
    }

    @GetMapping("/users/{id}")
    fun getUserDetail(@PathVariable("id") id: String): ResponseEntity<GetUserDetailUseCase.GetUserDetailByIdResponse> {
        val response = getUserDetailUseCase.getUserDetailById(GetUserDetailUseCase.GetUserDetailByIdRequest(id))
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                GetUserDetailUseCase.GetUserDetailByIdResponse(
                    data = response
                )
            )
    }

    @PutMapping("/users/{id}")
    @Authenticated
    fun updateUser(
        @PathVariable("id") id: String,
        @RequestBody @Valid request: UpdateUserUseCase.UpdateUserRequest
    ): ResponseEntity<UpdateUserUseCase.UpdateUserResponse> {
        val response = updateUserUseCase.updateUser(id, request)
        return ResponseEntity.status(HttpStatus.OK).body(
            UpdateUserUseCase.UpdateUserResponse(
                data = UpdateUserUseCase.Data(response)
            )
        )
    }

    @DeleteMapping("/users/{id}")
    @Authenticated
    fun deleteUser(@PathVariable("id") id: String): ResponseEntity<DeleteUserUseCase> {
        deleteUserUseCase.deleteUser(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }


}