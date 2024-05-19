package wisoft.io.quotation.application.port.out.user

import wisoft.io.quotation.domain.User

interface CreateUserPort {
    fun create(user: User): String
}
