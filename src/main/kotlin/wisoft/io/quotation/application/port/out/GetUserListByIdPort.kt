package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface GetUserListByIdPort {

    fun getUserListById(id: String): List<User>
}