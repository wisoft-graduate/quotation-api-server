package wisoft.io.quotation.application.port.out

interface GetExistUserByNicknamePort {

    fun getExistUserByNickname(nickname: String): Boolean

}