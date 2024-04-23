package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface CreateUserPort {
    fun create(user: User): String
}
