package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.SignUpUseCase
import wisoft.io.quotation.application.port.out.SaveUserPort
import wisoft.io.quotation.domain.User

@Service
class UserService(val saveUserPort: SaveUserPort): SignUpUseCase {

    override fun signUp(request: SignUpUseCase.SignUpRequest): String {
        val user = request.run {
            User(
                id = this.id,
                nickname = this.nickname,
                identityVerificationQuestion = this.identityVerificationQuestion,
                identityVerificationAnswer = this.identityVerificationAnswer
            )
        }

        user.encryptPassword(request.password)
        return saveUserPort.save(user)
    }

}