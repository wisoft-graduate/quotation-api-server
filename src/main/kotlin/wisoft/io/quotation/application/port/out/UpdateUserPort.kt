package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface UpdateUserPort {
    fun updateUser(user: User): String
}
