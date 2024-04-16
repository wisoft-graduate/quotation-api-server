package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface DeleteUserPort {

    fun delete(user: User)

}