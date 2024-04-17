package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface GetUserPort {

    fun getUserById(id: String): User?
    fun getUserByNickname(nickname: String): User?

}