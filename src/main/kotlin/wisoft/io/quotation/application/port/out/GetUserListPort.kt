package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.application.port.`in`.GetUserListUseCase
import wisoft.io.quotation.domain.User

interface GetUserListPort {
    fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<User>
}
