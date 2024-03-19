package wisoft.io.quotation.application.port.out

interface ExistUserPort {

    fun existUser(id: String): Boolean

}