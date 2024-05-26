package wisoft.io.quotation.application.port.out.user

import wisoft.io.quotation.domain.User

interface DeleteUserPort {
    fun deleteUser(user: User)
}
