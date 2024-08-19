package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.BookmarkEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.domain.Bookmark

@Component
class BookmarkMapper(
    val quotationRepository: QuotationRepository,
    val quotationMapper: QuotationMapper,
) : Mapper<BookmarkEntity, Bookmark> {
    override fun toDomain(entity: BookmarkEntity): Bookmark {
        return Bookmark(
            id = entity.id,
            name = entity.name,
            userId = entity.userId,
            quotations = quotationMapper.toQuotationDomains(quotationRepository.findAllById(entity.quotationIds)),
            visibility = entity.visibility,
            icon = entity.icon,
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
        )
    }

    override fun toEntity(domain: Bookmark): BookmarkEntity {
        return BookmarkEntity(
            id = domain.id,
            name = domain.name,
            userId = domain.userId,
            quotationIds = domain.quotations.map { it.id },
            visibility = domain.visibility,
            icon = domain.icon,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }
}
