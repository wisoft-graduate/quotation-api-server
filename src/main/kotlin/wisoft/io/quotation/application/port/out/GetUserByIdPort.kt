package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface GetUserByIdPort {

    fun getByIdOrNull(id: String): User?

}