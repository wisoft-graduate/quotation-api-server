package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.exception.error.AuthorNotFoundException

@Component
class QuotationMapper(val authorRepository: AuthorRepository, val authorMapper: AuthorMapper) :
    Mapper<QuotationEntity, Quotation> {
    fun toDomains(quotationEntityList: List<QuotationEntity>): List<Quotation> {
        val authorIds = quotationEntityList.map { it.authorId }.toSet()
        val authorList = authorRepository.findAllById(authorIds)
        val authorMap = authorList.associateBy { it.id }
        return quotationEntityList.map { entity ->
            Quotation(
                id = entity.id,
                author =
                    authorMap[entity.authorId]
                        ?.let { authorMapper.toDomain(it) }
                        ?: throw AuthorNotFoundException(entity.authorId.toString()),
                content = entity.content,
                likeCount = entity.likeCount,
                shareCount = entity.shareCount,
                commentCount = entity.commentCount,
                backgroundImagePath = entity.backgroundImagePath,
                createdTime = entity.createdTime,
                lastModifiedTime = entity.lastModifiedTime,
            )
        }
    }

    override fun toDomain(entity: QuotationEntity): Quotation {
        return Quotation(
            id = entity.id,
            author =
                authorRepository.findByIdOrNull(entity.authorId)
                    ?.let { authorMapper.toDomain(it) }
                    ?: throw AuthorNotFoundException(entity.authorId.toString()),
            content = entity.content,
            likeCount = entity.likeCount,
            shareCount = entity.shareCount,
            commentCount = entity.commentCount,
            backgroundImagePath = entity.backgroundImagePath,
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
        )
    }

    override fun toEntity(domain: Quotation): QuotationEntity {
        return QuotationEntity(
            id = domain.id,
            authorId = domain.author.id,
            content = domain.content,
            likeCount = domain.likeCount,
            shareCount = domain.shareCount,
            commentCount = domain.commentCount,
            backgroundImagePath = domain.backgroundImagePath,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }
}
