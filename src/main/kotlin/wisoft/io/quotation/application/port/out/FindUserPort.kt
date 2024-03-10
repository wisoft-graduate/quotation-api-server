package wisoft.io.quotation.application.port.out

import wisoft.io.quotation.domain.User

interface FindUserPort {

    fun findByIdOrNull(id: String): User
    fun findLeaveUsersCount(): Long

    fun existUser(id: String): Boolean

}