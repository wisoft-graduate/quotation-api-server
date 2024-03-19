package wisoft.io.quotation.application.port.out

interface ExistUserByNicknamePort {

    fun existUserByNickname(nickname: String): Boolean

}