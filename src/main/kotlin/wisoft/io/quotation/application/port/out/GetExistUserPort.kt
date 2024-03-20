package wisoft.io.quotation.application.port.out

interface GetExistUserPort {

    fun getExistUser(id: String): Boolean

}