package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.AuthorEntity
import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.exception.error.AuthorNotFoundException

@Component
class QuotationEntityMapper(val authorRepository: AuthorRepository) {
    fun toDomains(quotationEntityList: List<QuotationEntity>): List<Quotation> {
        val authorIds = quotationEntityList.map { it.authorId }.toSet()
        val authorList = authorRepository.findAllById(authorIds)
        val authorMap = authorList.associateBy { it.id }
        return quotationEntityList.map {
            toDomain(it, authorMap[it.authorId])
        }
    }

    fun toDomain(
        quotationEntity: QuotationEntity,
        author: AuthorEntity? = null,
    ): Quotation {
        return Quotation(
            id = quotationEntity.id,
            author =
                author?.toDomain()
                    ?: authorRepository.findByIdOrNull(quotationEntity.authorId)?.toDomain()
                    ?: throw AuthorNotFoundException(quotationEntity.authorId.toString()),
            content = quotationEntity.content,
            likeCount = quotationEntity.likeCount,
            shareCount = quotationEntity.shareCount,
            commentCount = quotationEntity.commentCount,
            backgroundImagePath = quotationEntity.backgroundImagePath,
            createdTime = quotationEntity.createdTime,
            lastModifiedTime = quotationEntity.lastModifiedTime,
        )
    }
}
