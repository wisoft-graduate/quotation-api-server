package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.AuthorEntity
import wisoft.io.quotation.domain.Author

@Component
class AuthorMapper : Mapper<AuthorEntity, Author> {
    override fun toDomain(entity: AuthorEntity): Author {
        return Author(
            id = entity.id,
            name = entity.name,
            countryCode = entity.countryCode,
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
        )
    }

    override fun toEntity(domain: Author): AuthorEntity {
        return AuthorEntity(
            id = domain.id,
            name = domain.name,
            countryCode = domain.countryCode,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }
}
