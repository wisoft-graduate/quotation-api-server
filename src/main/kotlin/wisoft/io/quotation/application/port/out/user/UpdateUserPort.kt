package wisoft.io.quotation.application.port.out.user

import wisoft.io.quotation.domain.User

interface UpdateUserPort {
    fun updateUser(user: User): String
}
