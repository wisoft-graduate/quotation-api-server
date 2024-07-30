package wisoft.io.quotation.adaptor.out.dispatcher.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.ObjectMetadata
import mu.KotlinLogging
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Component
import wisoft.io.quotation.exception.error.ProfileMetadataValidationException
import wisoft.io.quotation.exception.error.S3ObjectException
import wisoft.io.quotation.exception.error.S3ObjectNotFoundException
import wisoft.io.quotation.util.YmlConfig.Companion.readYmlFile
import java.io.ByteArrayInputStream
import java.util.*
import javax.imageio.ImageIO

@Component
class S3DispatcherImpl(
    private val amazonS3Client: AmazonS3,
) : S3Dispatcher {
    val logger = KotlinLogging.logger {}
    val bucketName: String = readYmlFile().cloud.aws.s3.bucket

    override fun createProfileImage(base64Image: String): String {
        return runCatching {
            val imageBytes = Base64.decodeBase64(base64Image)

            val maxFileSize = 5 * 1024 * 1024 // 5MB
            if (imageBytes.size > maxFileSize) {
                throw ProfileMetadataValidationException("image size:$imageBytes, File size exceeds the 5MB limit.")
            }
            val imageFormat = getImageFormat(imageBytes)
            if (imageFormat !in listOf("image/jpeg", "image/png", "image/gif")) {
                throw ProfileMetadataValidationException(
                    "image format: $imageFormat, Invalid file format. Only jpg, png, and gif are allowed.",
                )
            }

            val metadata = ObjectMetadata()
            metadata.contentLength = imageBytes.size.toLong()
            val imageId = UUID.randomUUID().toString()
            val inputStream = ByteArrayInputStream(imageBytes)
            amazonS3Client.putObject(bucketName, imageId, inputStream, metadata)
            imageId
        }.onFailure {
            logger.error { "uploadS3Image fail" }
        }.getOrThrow()
    }

    private fun getImageFormat(imageBytes: ByteArray): String {
        val inputStream = ByteArrayInputStream(imageBytes)
        val imageInputStream = ImageIO.createImageInputStream(inputStream)
        val readers = ImageIO.getImageReaders(imageInputStream)
        if (readers.hasNext()) {
            val reader = readers.next()
            reader.input = imageInputStream
            val formatName = reader.formatName.lowercase(Locale.getDefault())
            reader.dispose()
            return when (formatName) {
                "jpeg", "jpg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                else -> ""
            }
        }
        return ""
    }

    override fun getProfileImage(id: String): String {
        return runCatching {
            val s3Object = amazonS3Client.getObject(bucketName, id)
            val inputStream = s3Object.objectContent
            println(inputStream)
            val bytes = inputStream.readBytes()
            val string = Base64.encodeBase64String(bytes)
            println(string)
            string
        }.onFailure { ex ->
            logger.error { "getS3Image fail: param[id: $id]" }
            if (ex is AmazonS3Exception && ex.statusCode == 404) {
                throw S3ObjectNotFoundException("id: $id")
            } else {
                throw S3ObjectException("S3 Exception")
            }
        }.getOrThrow()
    }

    override fun deleteProfileImage(id: String) {
        return runCatching {
            if (!amazonS3Client.doesObjectExist(bucketName, id)) {
                throw S3ObjectNotFoundException("S3 object not found for deletion: $id")
            }

            amazonS3Client.deleteObject(bucketName, id)
        }.onFailure {
            logger.error { "deleteS3Image fail: param[id: $id]" }
        }.getOrThrow()
    }
}
