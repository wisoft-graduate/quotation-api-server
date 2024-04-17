package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface GetUserListPort {
    fun getUserList(nickname: String): List<User>
}