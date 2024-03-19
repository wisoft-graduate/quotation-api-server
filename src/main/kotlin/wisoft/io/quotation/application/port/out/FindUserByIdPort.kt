package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface FindUserByIdPort {

    fun findByIdOrNull(id: String): User

}