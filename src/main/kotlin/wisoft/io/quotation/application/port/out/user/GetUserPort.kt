package wisoft.io.quotation.application.port.out.user

import wisoft.io.quotation.domain.User

interface GetUserPort {
    fun getUserById(id: String): User?

    fun getUserByNickname(nickname: String): User?

    fun getUserByIdentityInformation(
        id: String,
        question: String,
        answer: String,
    ): User?
}
