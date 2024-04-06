package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface GetUserByNicknamePort {

    fun getByNicknameOrNull(nickname: String): User?
}