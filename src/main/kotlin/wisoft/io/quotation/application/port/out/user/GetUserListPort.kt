package wisoft.io.quotation.application.port.out.user

import wisoft.io.quotation.application.port.`in`.user.GetUserListUseCase
import wisoft.io.quotation.domain.User

interface GetUserListPort {
    fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<User>
}
