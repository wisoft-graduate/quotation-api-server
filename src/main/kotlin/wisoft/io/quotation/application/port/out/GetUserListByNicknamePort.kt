package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface GetUserListByNicknamePort {

    fun getUserListByNickname(nickname: String): List<User>
}