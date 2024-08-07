package wisoft.io.quotation.util

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

data class YmlConfig(
    @JsonProperty("server") val server: ServerConfig,
    @JsonProperty("spring") val spring: SpringConfig,
    @JsonProperty("environment") val environment: EnvironmentConfig,
    @JsonProperty("one-signal") val oneSignal: OneSignalConfig,
    @JsonProperty("cloud") val cloud: CloudConfig,
) {
    data class ServerConfig(
        @JsonProperty("port") val port: Long,
        @JsonProperty("servlet") val servlet: ServletConfig,
    ) {
        data class ServletConfig(
            @JsonProperty("encoding") val encoding: EncodingConfig,
        ) {
            data class EncodingConfig(
                @JsonProperty("charset") val charset: String,
                @JsonProperty("enabled") val enabled: Boolean,
                @JsonProperty("force") val force: Boolean,
            )
        }
    }

    data class SpringConfig(
        @JsonProperty("application") val application: ApplicationConfig,
        @JsonProperty("datasource") val datasource: DataSourceConfig,
        @JsonProperty("jpa") val jpa: JpaConfig,
        @JsonProperty("flyway") val flyway: FlywayConfig,
    ) {
        data class ApplicationConfig(
            @JsonProperty("name") val name: String,
        )

        data class DataSourceConfig(
            @JsonProperty("url") val url: String,
            @JsonProperty("username") val username: String,
            @JsonProperty("password") val password: String,
            @JsonProperty("driver-class-name") val driverClassName: String,
        )

        data class JpaConfig(
            @JsonProperty("hibernate") val hibernate: HibernateConfig,
            @JsonProperty("properties") val properties: PropertiesConfig,
            @JsonProperty("open-in-view") val openInView: Boolean,
        ) {
            data class HibernateConfig(
                @JsonProperty("ddl-auto") val ddlAuto: String,
            )

            data class PropertiesConfig(
                @JsonProperty("hibernate") val hibernate: PropertiesHibernateConfig,
            ) {
                data class PropertiesHibernateConfig(
                    @JsonProperty("format_sql") val formatSql: Boolean,
                    @JsonProperty("show_sql") val showSql: String,
                    @JsonProperty("dialect") val dialect: String,
                )
            }
        }

        data class FlywayConfig(
            @JsonProperty("enabled") val enabled: Boolean,
            @JsonProperty("locations") val locations: String,
            @JsonProperty("baseline-on-migrate") val baselineOnMigrate: Boolean,
        )
    }

    data class EnvironmentConfig(
        @JsonProperty("jwt") val jwt: JwtConfig,
    ) {
        data class JwtConfig(
            @JsonProperty("secret-key") val secretKey: String,
            @JsonProperty("access-token-expiration-time") val accessTokenExpirationTime: Long,
            @JsonProperty("refresh-token-expiration-time") val refreshTokenExpirationTime: Long,
            @JsonProperty("password-refresh-token-expiration-time")
            val passwordRefreshTokenExpirationTime: Long,
        )
    }

    data class OneSignalConfig(
        @JsonProperty("api-key") val apiKey: String,
        @JsonProperty("app-id") val appId: String,
    )

    data class CloudConfig(
        @JsonProperty("aws") val aws: AWSConfig,
    ) {
        data class AWSConfig(
            @JsonProperty("credentials") val credentials: CredentialsConfig,
            @JsonProperty("region") val region: RegionConfig,
            @JsonProperty("stack") val stack: StackConfig,
            @JsonProperty("s3") val s3: S3Config,
        ) {
            data class CredentialsConfig(
                @JsonProperty("accessKey") val accessKey: String,
                @JsonProperty("secretKey") val secretKey: String,
            )

            data class RegionConfig(
                @JsonProperty("static") val static: String,
            )

            data class StackConfig(
                @JsonProperty("auto") val auto: Boolean,
            )

            data class S3Config(
                @JsonProperty("bucket") val bucket: String,
            )
        }
    }

    companion object {
        fun readYmlFile(): YmlConfig {
            val objectMapper = ObjectMapper(YAMLFactory())
            val file = object {}.javaClass.getResource("/application.yaml")

            return objectMapper.readValue(file, YmlConfig::class.java)
        }
    }
}
