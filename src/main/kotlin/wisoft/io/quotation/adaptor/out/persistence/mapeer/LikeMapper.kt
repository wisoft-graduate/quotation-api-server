package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.LikeEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.domain.Like
import wisoft.io.quotation.exception.error.QuotationNotFoundException

@Component
class LikeMapper(val quotationRepository: QuotationRepository, val quotationMapper: QuotationMapper) :
    Mapper<LikeEntity, Like> {
    fun toDomains(likeEntityList: List<LikeEntity>): List<Like> {
        val quotationIds = likeEntityList.map { it.quotationId }
        val quotations = quotationRepository.findAllById(quotationIds).associateBy { it.id }
        likeEntityList.map { entity ->
            Like(
                id = entity.id,
                userId = entity.userId,
                quotation =
                    quotations[entity.quotationId]
                        ?.let { quotationMapper.toDomain(it) }
                        ?: throw QuotationNotFoundException(entity.quotationId.toString()),
            )
        }
        return likeEntityList.map { toDomain(it) }
    }

    override fun toDomain(entity: LikeEntity): Like {
        return Like(
            id = entity.id,
            userId = entity.userId,
            quotation =
                quotationMapper.toDomain(
                    quotationRepository.findByIdOrNull(entity.quotationId)
                        ?: throw QuotationNotFoundException(entity.quotationId.toString()),
                ),
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
        )
    }

    override fun toEntity(domain: Like): LikeEntity {
        return LikeEntity(
            id = domain.id,
            userId = domain.userId,
            quotationId = domain.quotation.id,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }
}
