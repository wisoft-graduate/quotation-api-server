package wisoft.io.quotation.adaptor.out.dispatcher

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.dispatcher.s3.S3Dispatcher
import wisoft.io.quotation.application.port.out.s3.CreateProfileImagePort
import wisoft.io.quotation.application.port.out.s3.DeleteProfileImagePort
import wisoft.io.quotation.application.port.out.s3.GetProfileImagePort

@Component
class S3Adaptor(
    private val s3Dispatcher: S3Dispatcher,
) : CreateProfileImagePort, GetProfileImagePort, DeleteProfileImagePort {
    override fun createProfileImage(base64Image: String): String {
        return s3Dispatcher.createProfileImage(base64Image)
    }

    override fun getProfileImage(id: String): String {
        return s3Dispatcher.getProfileImage(id)
    }

    override fun deleteProfileImage(id: String) {
        return s3Dispatcher.deleteProfileImage(id)
    }
}
