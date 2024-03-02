package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface SaveUserPort {

    fun save(user: User): String

}