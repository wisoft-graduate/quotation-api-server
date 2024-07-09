package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.LikeEntity
import wisoft.io.quotation.domain.Like

@Component
class LikeMapper : Mapper<LikeEntity, Like> {
    override fun toDomain(entity: LikeEntity): Like {
        return Like(
            id = entity.id,
            userId = entity.userId,
            quotationId = entity.quotationId,
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
        )
    }

    override fun toEntity(domain: Like): LikeEntity {
        return LikeEntity(
            id = domain.id,
            userId = domain.userId,
            quotationId = domain.quotationId,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }
}
