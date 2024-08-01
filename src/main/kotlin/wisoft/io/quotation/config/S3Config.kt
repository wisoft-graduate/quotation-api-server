package wisoft.io.quotation.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import wisoft.io.quotation.util.JWTUtil.readYmlFile

@Configuration
class S3Config(
    private val accessKey: String = readYmlFile().cloud.aws.credentials.accessKey,
    private val secretKey: String = readYmlFile().cloud.aws.credentials.secretKey,
    private val region: String = readYmlFile().cloud.aws.region.static,
) {
    @Bean
    fun amazonS3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .withCredentials(
                AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)),
            )
            .withRegion(region)
            .build()
    }
}
