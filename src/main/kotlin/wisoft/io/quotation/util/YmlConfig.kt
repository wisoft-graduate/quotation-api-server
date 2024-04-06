package wisoft.io.quotation.util

import com.fasterxml.jackson.annotation.JsonProperty

data class YmlConfig(
    @JsonProperty("server") val server: ServerConfig,
    @JsonProperty("spring") val spring: SpringConfig,
    @JsonProperty("environment") val environment: EnvironmentConfig
) {
    data class ServerConfig(
        @JsonProperty("port") val port: Long,
        @JsonProperty("servlet") val servlet: ServletConfig
    ) {
        data class ServletConfig(
            @JsonProperty("encoding") val encoding: EncodingConfig
        ) {
            data class EncodingConfig(
                @JsonProperty("charset") val charset: String,
                @JsonProperty("enabled") val enabled: Boolean,
                @JsonProperty("force") val force: Boolean
            )
        }
    }

    data class SpringConfig(
        @JsonProperty("application") val application: ApplicationConfig,
        @JsonProperty("datasource") val datasource: DataSourceConfig,
        @JsonProperty("jpa") val jpa: JpaConfig,
    ) {
        data class ApplicationConfig(
            @JsonProperty("name") val name: String
        )
        data class DataSourceConfig(
            @JsonProperty("url") val url: String,
            @JsonProperty("username") val username: String,
            @JsonProperty("password") val password: String,
            @JsonProperty("driver-class-name") val driverClassName: String
        )
        data class JpaConfig(
            @JsonProperty("hibernate") val hibernate: HibernateConfig,
            @JsonProperty("properties") val properties: PropertiesConfig,
            @JsonProperty("open-in-view") val openInView: Boolean
        ) {
            data class HibernateConfig(
                @JsonProperty("ddl-auto") val ddlAuto: String,
            )
            data class PropertiesConfig(
                @JsonProperty("hibernate") val hibernate: PropertiesHibernateConfig
            ) {
                data class PropertiesHibernateConfig(
                    @JsonProperty("format_sql") val formatSql: Boolean,
                    @JsonProperty("show_sql") val showSql: String,
                    @JsonProperty("dialect") val dialect: String,
                )
            }
        }
    }

    data class EnvironmentConfig(
        @JsonProperty("jwt") val jwt: JwtConfig,
    ) {
        data class JwtConfig(
            @JsonProperty("secret-key") val secretKey: String,
            @JsonProperty("access-token-expiration-time") val accessTokenExpirationTime: Long,
            @JsonProperty("refresh-token-expiration-time") val refreshTokenExpirationTime: Long
        )
    }
}
